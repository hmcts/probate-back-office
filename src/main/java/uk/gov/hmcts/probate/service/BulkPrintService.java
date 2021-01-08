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
import uk.gov.hmcts.probate.transformer.DocumentTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.sendletter.api.SendLetterApi;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.hmcts.reform.sendletter.api.model.v3.LetterV3;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;

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
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final EventValidationService eventValidationService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final DocumentTransformer documentTransformer;

    public SendLetterResponse sendToBulkPrintForGrant(CallbackRequest callbackRequest, Document grantDocument, Document coverSheet) {
        return sendToBulkPrint(callbackRequest, grantDocument, coverSheet, false);
    }
    
    public SendLetterResponse sendToBulkPrintForCaveat(CaveatCallbackRequest caveatCallbackRequest, Document grantDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = serviceAuthTokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, caveatCallbackRequest.getCaseDetails().getId());

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

    public String optionallySendToBulkPrint(CallbackRequest callbackRequest, Document coversheet, Document document,
                                            boolean sendToBulkPrint) {
        CallbackResponse response;
        SendLetterResponse sendLetterResponse;
        String letterId = null;
        if (sendToBulkPrint) {
            log.info("Initiate call to bulk print for document with case id {} and coversheet",
                    callbackRequest.getCaseDetails().getId());
            sendLetterResponse = sendToBulkPrintForGrant(callbackRequest, document, coversheet);
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

    public SendLetterResponse sendDocumentsForReprint(CallbackRequest callbackRequest, Document selectedDocument, Document coverSheet) {
        SendLetterResponse sendLetterResponse = sendToBulkPrint(callbackRequest, selectedDocument, coverSheet, true);
        String letterId = sendLetterResponse != null ? sendLetterResponse.letterId.toString() : null;
        CallbackResponse response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
        if (!response.getErrors().isEmpty()) {
            throw new BulkPrintException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                "bulkPrintResponseNull").getMessage(),
                "Bulk print send letter for reprint response is null for: " + callbackRequest.getCaseDetails().getId());
        }
        return sendLetterResponse;
    }

    private SendLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document grantDocument, Document coverSheet, boolean forReprint) {
        SendLetterResponse sendLetterResponse = null;
        try {
            String authHeaderValue = serviceAuthTokenGenerator.generate();

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put(ADDITIONAL_DATA_CASE_REFERENCE, callbackRequest.getCaseDetails().getId());

            additionalData = Collections.unmodifiableMap(additionalData);

            List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> pdfs;
            if (forReprint) {
                pdfs = arrangePdfDocumentsForReprint(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            } else {
                pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            }
            log.info(CASE_ID + callbackRequest.getCaseDetails().getId().toString() + "number of documents is: " + pdfs.size());

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
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(Arrays.asList(grantDocument), WELSH_INTESTACY_GRANT_REISSUE)) {
            extraCopies = addAdditionalCopiesForGrants(callbackRequest);
        }

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document coversheetDocument = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedCoverSheet, 1);
        uk.gov.hmcts.reform.sendletter.api.model.v3.Document document = new uk.gov.hmcts.reform.sendletter.api.model.v3.Document(encodedGrantDocument, extraCopies.intValue());

        documents.add(coversheetDocument);
        documents.add(document);

        return documents;
    }

    private List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> arrangePdfDocumentsForReprint(CallbackRequest callbackRequest,
                                                                                                          Document grantDocument,
                                                                                                          Document coverSheetDocument,
                                                                                                          String authHeaderValue) throws IOException {
        Long extraCopies = Long.parseLong(callbackRequest.getCaseDetails().getData().getReprintNumberOfCopies());
        List<uk.gov.hmcts.reform.sendletter.api.model.v3.Document> documents = new LinkedList<>();
        String encodedCoverSheet = getPdfAsBase64EncodedString(coverSheetDocument, authHeaderValue, callbackRequest);
        String encodedGrantDocument = getPdfAsBase64EncodedString(grantDocument, authHeaderValue, callbackRequest);
        
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

    private Long addAdditionalCopiesForGrants(CallbackRequest callbackRequest) {
        Long extraCopiesOfGrant = 1L;
        if (callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() != null) {
            extraCopiesOfGrant = callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() + 1;
        }
        return extraCopiesOfGrant;
    }

}
