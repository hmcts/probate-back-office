package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.probate.exception.BulkPrintException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.client.DocumentStoreClient;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
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

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Service
@Slf4j
@AllArgsConstructor
public class BulkPrintService {

    private static final String XEROX_TYPE_PARAMETER = "PRO001";
    private static final String BEARER = "Bearer ";
    private static final String ADDITIONAL_DATA_CASE_REFERENCE = "caseReference";
    private static final String CASE_ID = "case id ";
    @Autowired
    private BusinessValidationMessageService businessValidationMessageService;
    private final SendLetterApi sendLetterApi;
    private final DocumentStoreClient documentStoreClient;
    private final ServiceAuthTokenGenerator tokenGenerator;
    private final EventValidationService eventValidationService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;

    public SendLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, callbackRequest.getCaseDetails().getId());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<String> pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            getDocumentSize(pdfs, callbackRequest);

            sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                    new LetterWithPdfsRequest(pdfs, XEROX_TYPE_PARAMETER, additionalData));
            log.info("Letter service produced the following letter Id {} for a pdf size {} for the case id {}",
                    sendLetterResponse.letterId, pdfs.size(), callbackRequest.getCaseDetails().getId());

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

    public SendLetterResponse sendToBulkPrint(CaveatCallbackRequest caveatCallbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, caveatCallbackRequest.getCaseDetails().getId());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<String> pdfs = arrangePdfDocumentsForBulkPrinting(caveatCallbackRequest, grantDocument, coverSheet, authHeaderValue);

            sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                    new LetterWithPdfsRequest(pdfs, XEROX_TYPE_PARAMETER, additionalData));
            log.info("Letter service produced the following letter Id {} for a pdf size {} for the case id {}",
                    sendLetterResponse.letterId, pdfs.size(), caveatCallbackRequest.getCaseDetails().getId());

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

    public String sendToBulkPrint(CallbackRequest callbackRequest, Document coversheet, Document document,
                                  boolean sendToBulkPrint) {
        CallbackResponse response;
        SendLetterResponse sendLetterResponse = null;
        String letterId = null;
        if (sendToBulkPrint) {
            log.info("Initiate call to bulk print for document with case id {} and coversheet",
                    callbackRequest.getCaseDetails().getId());
            sendLetterResponse = sendToBulkPrint(callbackRequest, document, coversheet);
            letterId = sendLetterResponse != null
                    ? sendLetterResponse.letterId.toString()
                    : null;
            response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
            if (!response.getErrors().isEmpty()) {
                throw new BulkPrintException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                        "bulkPrintResponseNull").getMessage(),
                        "Bulk print send letter response is null for: " + callbackRequest.getCaseDetails().getId());
            }
        }
        return letterId;
    }


    private String getPdfAsBase64EncodedString(Document document,
                                               String authHeaderValue,
                                               CallbackRequest callbackRequest) throws IOException {

        String response = Base64.getEncoder().encodeToString(documentStoreClient.retrieveDocument(document, authHeaderValue));
        log.info(CASE_ID + callbackRequest.getCaseDetails().getId().toString()
                + "dm store" + document.getDocumentFileName());
        return response;
    }

    private String getPdfAsBase64EncodedString(Document document,
                                               String authHeaderValue,
                                               CaveatCallbackRequest caveatCallbackRequest) throws IOException {

        String response = Base64.getEncoder().encodeToString(documentStoreClient.retrieveDocument(document, authHeaderValue));
        log.info(CASE_ID + caveatCallbackRequest.getCaseDetails().getId().toString()
                + "dm store" + document.getDocumentFileName());
        return response;
    }

    private List<String> arrangePdfDocumentsForBulkPrinting(CallbackRequest callbackRequest,
                                                            Document grantDocument,
                                                            Document coverSheetDocument,
                                                            String authHeaderValue) throws IOException {
        List<String> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue, callbackRequest);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, callbackRequest);

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

    private List<String> arrangePdfDocumentsForBulkPrinting(CaveatCallbackRequest caveatCallbackRequest,
                                                            Document grantDocument,
                                                            Document coverSheetDocument,
                                                            String authHeaderValue) throws IOException {
        List<String> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue, caveatCallbackRequest);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, caveatCallbackRequest);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        documents.add(encodedCoverSheet);
        documents.add(encodedGrantDocument);
        return documents;
    }

    private List<String> getDocumentSize(List<String> documents, CallbackRequest callbackRequest) throws IOException {
        log.info(CASE_ID + callbackRequest.getCaseDetails().getId().toString() + "number of documents is: " + documents.size());
        return documents;
    }
}
