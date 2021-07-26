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
import uk.gov.hmcts.reform.printletter.api.PrintLetterApi;
import uk.gov.hmcts.reform.printletter.api.PrintLetterResponse;
import uk.gov.hmcts.reform.printletter.api.model.v1.PrintDocument;
import uk.gov.hmcts.reform.printletter.api.model.v1.PrintLetterRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

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
    private final PrintLetterApi printLetterApi;
    private final DocumentStoreClient documentStoreClient;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final EventValidationService eventValidationService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final DocumentTransformer documentTransformer;
    @Autowired
    private BusinessValidationMessageService businessValidationMessageService;

    public PrintLetterResponse sendToBulkPrintForGrant(CallbackRequest callbackRequest, Document grantDocument,
            Document coverSheet) {
        return sendToBulkPrint(callbackRequest, grantDocument, coverSheet, false);
    }

    public PrintLetterResponse sendToBulkPrintForCaveat(CaveatCallbackRequest caveatCallbackRequest,
            Document grantDocument, Document coverSheet) {
        PrintLetterResponse printLetterResponse = null;
        var caseId = caveatCallbackRequest.getCaseDetails().getId().toString();
        try {
            var authHeaderValue = serviceAuthTokenGenerator.generate();
            var pdfs = arrangePdfDocumentsForBulkPrinting(
                    caveatCallbackRequest,
                    grantDocument,
                    coverSheet,
                    authHeaderValue);

            var printLetterRequest = new PrintLetterRequest(
                    XEROX_TYPE_PARAMETER,
                    pdfs,
                    caseId,
                    caseId,
                    getLetterType.apply(coverSheet, grantDocument));

            printLetterResponse = printLetterApi
                    .printLetter(BEARER + authHeaderValue, printLetterRequest);

            log.info("Letter service produced the following letter Id {} for a pdf size {} for the case id {}",
                    printLetterResponse.letterId, pdfs.size(), caveatCallbackRequest.getCaseDetails().getId());

        } catch (HttpClientErrorException ex) {
            log.error("Error with Http Connection to Bulk Print with response body {} and message {} and code {}",
                    ex.getResponseBodyAsString(),
                    ex.getLocalizedMessage(),
                    ex.getStatusCode());
        } catch (IOException ioe) {
            log.error("Error retrieving document from store with caseID {}",caseId, ioe);
        } catch (Exception e) {
            log.error("Error sending pdfs to bulk print {}", e.getMessage());
        }
        return printLetterResponse;
    }

    public String optionallySendToBulkPrint(CallbackRequest callbackRequest, Document coversheet, Document document,
            boolean sendToBulkPrint) {
        CallbackResponse response;
        PrintLetterResponse printLetterResponse;
        String letterId = null;
        if (sendToBulkPrint) {
            log.info("Initiate call to bulk print for document with case id {} and coversheet",
                    callbackRequest.getCaseDetails().getId());
            printLetterResponse = sendToBulkPrintForGrant(callbackRequest, document, coversheet);
            letterId = printLetterResponse != null
                    ? printLetterResponse.letterId.toString()
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

    public PrintLetterResponse sendDocumentsForReprint(CallbackRequest callbackRequest, Document selectedDocument,
            Document coverSheet) {
        var printLetterResponse = sendToBulkPrint(callbackRequest, selectedDocument, coverSheet, true);
        var letterId = printLetterResponse != null ? printLetterResponse.letterId.toString() : null;
        CallbackResponse response =
                eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
        if (!response.getErrors().isEmpty()) {
            throw new BulkPrintException(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "bulkPrintResponseNull").getMessage(),
                    "Bulk print send letter for reprint response is null for: "
                            + callbackRequest.getCaseDetails().getId());
        }
        return printLetterResponse;
    }

    private PrintLetterResponse sendToBulkPrint(CallbackRequest callbackRequest, Document grantDocument,
            Document coverSheet, boolean forReprint) {
        PrintLetterResponse printLetterResponse = null;
        String caseId = null;
        try {
            var authHeaderValue = serviceAuthTokenGenerator.generate();
            caseId = callbackRequest.getCaseDetails().getId().toString();

            List<PrintDocument> pdfs;
            if (forReprint) {
                pdfs = arrangePdfDocumentsForReprint(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            } else {
                pdfs = arrangePdfDocumentsForBulkPrinting(callbackRequest, grantDocument, coverSheet, authHeaderValue);
            }
            log.info("caseId {} number of documents is {}", caseId, pdfs.size());

            var printLetterRequest = new PrintLetterRequest(
                    XEROX_TYPE_PARAMETER,
                    pdfs,
                    caseId,
                    caseId,
                    getLetterType.apply(coverSheet, grantDocument));

            printLetterResponse = printLetterApi
                    .printLetter(BEARER + authHeaderValue, printLetterRequest);

            log.info("Letter service produced the following letter Id {} for a pdf size {} for the case id {}",
                    printLetterResponse.letterId, pdfs.size(), caseId);

        } catch (HttpClientErrorException ex) {
            log.error("Error with Http Connection to Bulk Print with response body {} and message {} and code {}",
                    ex.getResponseBodyAsString(),
                    ex.getLocalizedMessage(),
                    ex.getStatusCode());
        } catch (IOException ioe) {
            log.error("Error retrieving document from store with caseid {}",caseId, ioe);
        } catch (Exception e) {
            log.error("Error sending pdfs to bulk print {}", e.getMessage());
        }
        return printLetterResponse;
    }

    private List<PrintDocument> arrangePdfDocumentsForBulkPrinting(
            CallbackRequest callbackRequest,
            Document grantDocument,
            Document coverSheetDocument,
            String authHeaderValue) throws IOException {
        Long extraCopies = 1L;
        log.info("Rishi 1 coversheet filename {}",coverSheetDocument.getDocumentFileName());
        log.info("Rishi 1 grant filename {}",grantDocument.getDocumentFileName());
        var caseId = callbackRequest.getCaseDetails().getId().toString();

        byte[] coverBytes = getPdfAsBinary(coverSheetDocument, authHeaderValue, "Grant", caseId);
        byte[] grantBytes = getPdfAsBinary(grantDocument, authHeaderValue, "Grant", caseId);
        List<Document> documentList = Collections.singletonList(grantDocument);
        if (documentTransformer.hasDocumentWithType(documentList, DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documentList, INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documentList, ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documentList, DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documentList, WELSH_INTESTACY_GRANT_REISSUE)) {
            extraCopies = addAdditionalCopiesForGrants(callbackRequest);
        }

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        var coversheet = new PrintDocument(
                coverSheetDocument.getDocumentFileName(),
                coverBytes,
                1);
        var grant = new PrintDocument(grantDocument.getDocumentFileName(),
                grantBytes,
                extraCopies.intValue());
        List<PrintDocument> documents = new LinkedList<>();
        log.info("Rishi coversheet filename {}",coversheet.fileName);
        log.info("Rishi grant filename {}",grant.fileName);
        documents.add(coversheet);
        documents.add(grant);
        return documents;
    }

    private List<PrintDocument> arrangePdfDocumentsForBulkPrinting(
            CaveatCallbackRequest caveatCallbackRequest,
            Document grantDocument,
            Document coverSheetDocument,
            String authHeaderValue)
            throws IOException {
        var caseId = caveatCallbackRequest.getCaseDetails().getId().toString();

        byte[] coverBytes = getPdfAsBinary(coverSheetDocument, authHeaderValue, "Caveat", caseId);
        byte[] grantBytes = getPdfAsBinary(grantDocument, authHeaderValue, "Caveat", caseId);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        var coversheet = new PrintDocument(
                coverSheetDocument.getDocumentFileName(),
                coverBytes,
                1);
        var grant = new PrintDocument(grantDocument.getDocumentFileName(),
                grantBytes,
                1);
        List<PrintDocument> documents = new LinkedList<>();
        documents.add(coversheet);
        documents.add(grant);

        return documents;
    }

    private List<PrintDocument> arrangePdfDocumentsForReprint(
            CallbackRequest callbackRequest,
            Document grantDocument,
            Document coverSheetDocument,
            String authHeaderValue)
        throws IOException {

        var caseId = callbackRequest.getCaseDetails().getId().toString();
        Long extraCopies = Long.parseLong(callbackRequest.getCaseDetails().getData().getReprintNumberOfCopies());

        byte[] coverBytes = getPdfAsBinary(coverSheetDocument, authHeaderValue, "Grant Reprint", caseId);
        byte[] grantBytes = getPdfAsBinary(grantDocument, authHeaderValue, "Grant Reprint", caseId);

        //Layer documents as cover letter first, grant, and extra copies of grant to PA.
        var coversheet = new PrintDocument(
                coverSheetDocument.getDocumentFileName(),
                coverBytes,
                1);
        var grant = new PrintDocument(grantDocument.getDocumentFileName(),
                grantBytes,
                extraCopies.intValue());

        List<PrintDocument> documents = new LinkedList<>();
        documents.add(coversheet);
        documents.add(grant);

        return documents;
    }

    private byte[] getPdfAsBinary(final Document document,
            final String authHeaderValue,
            final String docType,
            final String caseId) throws IOException {

        log.info("Type {} :: caseId {} coverSheetDocument dm store {}",
                docType, caseId, document.getDocumentFileName());
        return documentStoreClient.retrieveDocument(document, authHeaderValue);

    }

    private Long addAdditionalCopiesForGrants(CallbackRequest callbackRequest) {
        Long extraCopiesOfGrant = 1L;
        if (callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() != null) {
            extraCopiesOfGrant = callbackRequest.getCaseDetails().getData().getExtraCopiesOfGrant() + 1;
        }
        return extraCopiesOfGrant;
    }

    private final BiFunction<Document, Document, String> getLetterType = (cover, content) ->
            cover.getDocumentType().getTemplateName()
                    + "-"
                    + content.getDocumentType().getTemplateName();
}
