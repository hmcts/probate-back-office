package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class DocumentController {

    private final PDFManagementService pdfManagementService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final RegistriesProperties registriesProperties;
    private static final String GRANT_OF_PROBATE = "gop";
    private static final String ADMON_WILL = "admonWill";
    private static final String INTESTACY = "intestacy";
    private static final String EDGE_CASE = "edgeCase";
    private final BulkPrintService bulkPrintService;

    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        Document document;
        DocumentType template;
        getRegistryDetails(caseDetails);

        switch (caseData.getCaseType()) {
            case INTESTACY:
                template = INTESTACY_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
            case ADMON_WILL:
                template = ADMON_WILL_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
            case EDGE_CASE:
                document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case GRANT_OF_PROBATE:
            default:
                template = DIGITAL_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
        }

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        @Valid CaseData caseData = caseDetails.getData();
        DocumentType template;
        Document digitalGrantDocument;
        getRegistryDetails(caseDetails);

        switch (caseData.getCaseType()) {
            case EDGE_CASE:
                digitalGrantDocument = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case INTESTACY:
                template = INTESTACY_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
            case ADMON_WILL:
                template = ADMON_WILL_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
            case GRANT_OF_PROBATE:
            default:
                template = DIGITAL_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                break;
        }

        if (caseData.isSendForBulkPrintingRequested() && !caseData.getCaseType().equals(EDGE_CASE)) {
            SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, digitalGrantDocument);
            String letterId = response != null
                    ? response.letterId.toString()
                    : StringUtils.EMPTY;
            callbackResponseTransformer.transformWithBulkPrintComplete(callbackRequest, letterId);
        }

        List<Document> documents = new ArrayList<>();
        documents.add(digitalGrantDocument);

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }
        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            Document grantIssuedSentEmail = notificationService.sendEmail(GRANT_ISSUED, caseDetails);
            documents.add(grantIssuedSentEmail);
        }
        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest, documents));
    }

    private CaseDetails getRegistryDetails(CaseDetails caseDetails) {
        Registry registry = registriesProperties.getRegistries().get(
                caseDetails.getData().getRegistryLocation().toLowerCase());
        caseDetails.setRegistryTelephone(registry.getPhone());
        caseDetails.setRegistryAddressLine1(registry.getAddressLine1());
        caseDetails.setRegistryAddressLine2(registry.getAddressLine2());
        caseDetails.setRegistryPostcode(registry.getPostcode());
        caseDetails.setRegistryTown(registry.getTown());

        Registry ctscRegistry = registriesProperties.getRegistries().get("ctsc");
        caseDetails.setCtscTelephone(ctscRegistry.getPhone());

        return caseDetails;
    }

}
