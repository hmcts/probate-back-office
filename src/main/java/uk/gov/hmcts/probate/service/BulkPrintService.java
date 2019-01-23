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

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public SendLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document document) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("caseData", callbackRequest.getCaseDetails().getData());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<String> pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, document, authHeaderValue);

            sendLetterResponse = sendLetterApi.sendLetter(authHeaderValue,
                    new LetterWithPdfsRequest(pdfs, XEROX_TYPE_PARAMETER, additionalData));
            log.info("Letter service produced the following letter Id {} for a pdf size {}",
                    sendLetterResponse.letterId, pdfs.size());
        } catch (HttpClientErrorException ex) {
            log.error("Error with Http Connection to Bulk Print with response body {} and message {} and code {}",
                    ex.getResponseBodyAsString(),
                    ex.getLocalizedMessage(),
                    ex.getStatusCode());
        } catch (IOException ioe) {
            log.error("Error retrieving document from store with url {}",
                    document.getDocumentLink().getDocumentUrl(), ioe);
        } catch (Exception e) {
            log.error("Error sending pdfs to bulk print {}", e.getMessage());
        }
        return sendLetterResponse;
    }

    private String getPdfAsBase64EncodedString(Document caseDocument, String authHeaderValue) throws IOException {
        return Base64.getEncoder().encodeToString(documentStoreClient.retrieveDocument(caseDocument, authHeaderValue));
    }

    private List<String> arrangePdfDocumentsForBulkPrinting(CallbackRequest callbackRequest,
                                                            Document caseDocument,
                                                            String authHeaderValue) throws IOException {
        List<String> documents = new LinkedList<>();
        String encodedGrantDocument = getPdfAsBase64EncodedString(caseDocument, authHeaderValue);
        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        documents.add(encodeToString(pdfGeneratorService.generatePdf(DocumentType.GRANT_COVER, toJson(callbackRequest)).getBytes()));
        documents.add(encodedGrantDocument);
        Long extraCopiesOfGrant = 0L;
        if (callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() != null) {
            extraCopiesOfGrant = callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant();
        }
        LongStream.range(1, extraCopiesOfGrant + 1)
            .forEach(i -> documents.add(encodedGrantDocument));
        return documents;
    }

    private String encodeToString(byte[]  data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error converting Json data to string {} ", e.getMessage(), e);
            throw new BadRequestException(e.getMessage(), null);
        }
    }
}
