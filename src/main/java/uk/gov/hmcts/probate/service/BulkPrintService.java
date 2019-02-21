package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
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
    private static final String BEARER = "Bearer ";
    private final SendLetterApi sendLetterApi;
    private final DocumentStoreClient documentStoreClient;
    private final ServiceAuthTokenGenerator tokenGenerator;

    public SendLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("caseData", callbackRequest.getCaseDetails().getData());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<String> pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            getDocumentSize(pdfs);

            sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                    new LetterWithPdfsRequest(pdfs, XEROX_TYPE_PARAMETER, additionalData));
            log.info("Letter service produced the following letter Id {} for a pdf size {}",
                    sendLetterResponse.letterId, pdfs.size());

        } catch (HttpClientErrorException ex) {
            log.error("Error with Http Connection to Bulk Print with response body {} and message {} and code {}",
                    ex.getResponseBodyAsString(),
                    ex.getLocalizedMessage(),
                    ex.getStatusCode());
        } catch (IOException ioe) {
            log.error("Error retrieving document from store with url {}", ioe);
        } catch (Exception e) {
            log.error("Error sending pdfs to bulk print {}", e.getMessage());
        }
        return sendLetterResponse;
    }

    private String getPdfAsBase64EncodedString(Document document, String authHeaderValue) throws IOException {
        String response = Base64.getEncoder().encodeToString(documentStoreClient.retrieveDocument(document, authHeaderValue));
        log.info("dm store" + document.getDocumentFileName() + " string: " + response);
        return response;
    }

    private List<String> arrangePdfDocumentsForBulkPrinting(CallbackRequest callbackRequest,
                                                            Document grantDocument,
                                                            Document coverSheetDocument,
                                                            String authHeaderValue) throws IOException {
        List<String> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        documents.add(encodedCoverSheet);
        documents.add(encodedGrantDocument);

        Long extraCopiesOfGrant = 0L;
        if (callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() != null) {
            extraCopiesOfGrant = callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant();
        }
        LongStream.range(1, extraCopiesOfGrant + 1)
                .forEach(i -> documents.add(encodedGrantDocument));
        return documents;
    }

    private List<String> getDocumentSize(List<String> documents) throws IOException {
        log.info("number of documents is: " + documents.size());
        return documents;
    }
}
