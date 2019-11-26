package uk.gov.hmcts.probate.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;
import uk.gov.hmcts.probate.exception.*;
import uk.gov.hmcts.probate.model.ccd.caveat.request.*;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.*;
import uk.gov.hmcts.probate.model.ccd.raw.response.*;
import uk.gov.hmcts.probate.service.client.*;
import uk.gov.hmcts.probate.transformer.*;
import uk.gov.hmcts.probate.validator.*;
import uk.gov.hmcts.reform.authorisation.generators.*;
import uk.gov.hmcts.reform.sendletter.api.*;
import uk.gov.hmcts.reform.sendletter.api.model.v3.*;

import java.io.*;
import java.util.*;

import static uk.gov.hmcts.probate.model.Constants.*;
import static uk.gov.hmcts.probate.model.DocumentType.*;

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
    private final DocumentTransformer documentTransformer;

    public SendLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = getAdditionalData(callbackRequest.getCaseDetails().getId());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            getDocumentSize(pdfs, callbackRequest);

            sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                    new LetterV3(XEROX_TYPE_PARAMETER, pdfs, additionalData));
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

    private Map<String, Object> getAdditionalData(Long id) {
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, id);

        additionalData = Collections.unmodifiableMap(additionalData);
        return additionalData;
    }

    public SendLetterResponse sendToBulkPrint(CaveatCallbackRequest caveatCallbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = tokenGenerator.generate();

            Map<String, Object> additionalData = getAdditionalData(caveatCallbackRequest.getCaseDetails().getId());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> pdfs = arrangePdfDocumentsForBulkPrinting(caveatCallbackRequest, grantDocument, coverSheet, authHeaderValue);

            sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                    new LetterV3(XEROX_TYPE_PARAMETER, pdfs, additionalData));
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

    private List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> arrangePdfDocumentsForBulkPrinting(CallbackRequest callbackRequest,
                                                            Document grantDocument,
                                                            Document coverSheetDocument,
                                                            String authHeaderValue) throws IOException {
        Long extraCopies = 1L;
        List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue, callbackRequest);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, callbackRequest);

        if (documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), DIGITAL_GRANT_REISSUE)) {
            extraCopies = addAdditionalCopiesForGrantsPlusOriginal(callbackRequest);
        }

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document coversheetDocument = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedCoverSheet, 1);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document document = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedGrantDocument, extraCopies.intValue());

        documents.add(coversheetDocument);
        documents.add(document);

        return documents;
    }

    private List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> arrangePdfDocumentsForBulkPrinting(CaveatCallbackRequest caveatCallbackRequest,
                                                            Document grantDocument,
                                                            Document coverSheetDocument,
                                                            String authHeaderValue) throws IOException {
        List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue, caveatCallbackRequest);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, caveatCallbackRequest);

        uk.gov.hmcts.reform.sendletter.api.model.v3.Document coversheetDocument = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedCoverSheet, 1);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document document = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedGrantDocument, 1);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        documents.add(coversheetDocument);
        documents.add(document);
        return documents;
    }

    private Long addAdditionalCopiesForGrantsPlusOriginal(CallbackRequest callbackRequest) {
        Long extraCopiesOfGrant = 1L;
        if (callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() != null) {
            extraCopiesOfGrant = callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() + 1;
        }
        return extraCopiesOfGrant;
    }

    private List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> getDocumentSize(List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> documents, CallbackRequest callbackRequest) {
        log.info(CASE_ID + callbackRequest.getCaseDetails().getId().toString() + "number of documents is: " + documents.size());
        return documents;
    }



    public void sendGrantToThirdParties(CallbackRequest callbackRequest,
                                                            Document grantDocument,
                                                            byte[] coverSheetDocument,
                                                            Document willDocument,
                                                            String authHeaderValue) throws IOException {

        List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> documents = new LinkedList<>();
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, callbackRequest);
        String encodedWillDocument = getPdfAsBase64EncodedString(willDocument, authHeaderValue, callbackRequest);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        String coversheet = Arrays.toString(coverSheetDocument);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document coversheetDocument = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(coversheet, 1);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document document = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedGrantDocument, 1);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document will = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedWillDocument, 1);

        documents.add(coversheetDocument);
        documents.add(document);
        documents.add(will);


        Map<String, Object> additionalData = getAdditionalData(callbackRequest.getCaseDetails().getId());
        SendLetterResponse sendLetterResponse = null;
        String letterId = null;
        sendLetterResponse = sendLetterApi.sendLetter(BEARER + authHeaderValue,
                new LetterV3(XEROX_TYPE_PARAMETER, documents, additionalData));
        log.info("Letter service produced the following letter Id {} for the case id {} for third party",
                sendLetterResponse.letterId, callbackRequest.getCaseDetails().getId());
    }
}
