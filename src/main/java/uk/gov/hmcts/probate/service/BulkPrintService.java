package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.probate.service.template.pdf.PDFGeneratorService;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.LetterWithPdfsRequest;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Base64;
import java.util.stream.LongStream;

@Service
@Slf4j
@AllArgsConstructor
public class BulkPrintService {
    private static final String XEROX_TYPE_PARAMETER = "PRO001";
    private final SendLetterApi sendLetterApi;
    private final DocumentStoreClient documentStoreClient;
    private final ObjectMapper objectMapper;
    private final ServiceAuthTokenGenerator tokenGenerator;
    private final PDFGeneratorService pdfGeneratorService;

    public List<String> sendToBulkPrint(CallbackRequest callbackRequest, Document document) {
        List<String> pdfs = new LinkedList<>();
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("caseData", callbackRequest.getCaseDetails().getData());

            additionalData = Collections.unmodifiableMap(additionalData);

            pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, document, authHeaderValue);

            SendLetterResponse sendLetterResponse = sendLetterApi.sendLetter(authHeaderValue,
                    new LetterWithPdfsRequest(pdfs, XEROX_TYPE_PARAMETER, additionalData));
            log.info("Letter service produced the following letter Id {} for a case {}",
                    sendLetterResponse.letterId);
        } catch (HttpClientErrorException ex) {
            log.error(ex.getResponseBodyAsString() + ' ' + ex.getLocalizedMessage() + ' ' + ex.getStatusCode());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pdfs;
    }

    private String getPdfAsBase64EncodedString(Document caseDocument, String authHeaderValue) {
        return Base64.getEncoder().encodeToString(documentStoreClient.retrieveDocument(caseDocument, authHeaderValue));
    }

    private List<String> arrangePdfDocumentsForBulkPrinting(CallbackRequest callbackRequest,
                                                            Document caseDocument,
                                                            String authHeaderValue) {
        List<String> documents = new LinkedList<>();
        String encodedGrantDocument = getPdfAsBase64EncodedString(caseDocument, authHeaderValue);
        documents.add(encodeToString(pdfGeneratorService.generatePdf(DocumentType.GRANT_COVER, toJson(callbackRequest)).getBytes()));
        LongStream.range(1, callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() + 1)
                .forEach(i -> documents.add(encodedGrantDocument));
        documents.add(encodeToString(pdfGeneratorService.generatePdf(DocumentType.BLANK, toJson(callbackRequest)).getBytes()));
        documents.add(encodedGrantDocument);
        documents.add(encodeToString(pdfGeneratorService.generatePdf(DocumentType.BLANK, toJson(callbackRequest)).getBytes()));
        documents.add(encodedGrantDocument);
        return documents;
    }

    private String encodeToString(byte[]  data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage(), null);
        }
    }
}
