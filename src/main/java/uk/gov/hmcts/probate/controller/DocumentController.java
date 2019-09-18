package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistryDetailsService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.LONDON;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.ADMON_WILL_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.EDGE_CASE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class DocumentController {

    @Autowired
    private final DocumentGeneratorService documentGeneratorService;
    private final RegistryDetailsService registryDetailsService;
    private final PDFManagementService pdfManagementService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final WillLodgementCallbackResponseTransformer willLodgementCallbackResponseTransformer;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final RegistriesProperties registriesProperties;
    private final BulkPrintService bulkPrintService;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    private static final String DRAFT = "preview";
    private static final String FINAL = "final";

    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        Document document;
        DocumentType template;
        registryDetailsService.getRegistryDetails(caseDetails);

        switch (caseData.getCaseType()) {
            case INTESTACY_NAME:
                template = INTESTACY_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Intestacy grant preview document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
            case ADMON_WILL_NAME:
                template = ADMON_WILL_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Admon Will grant preview document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
            case EDGE_CASE_NAME:
                document = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case GRANT_OF_PROBATE_NAME:
            default:
                template = DIGITAL_GRANT_DRAFT;
                document = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Grant of Probate preview document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
        }

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT,
                                        DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT_REISSUE_DRAFT,
                                        ADMON_WILL_GRANT_REISSUE_DRAFT, DIGITAL_GRANT_REISSUE};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(
            @Validated({EmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        @Valid CaseData caseData = caseDetails.getData();
        DocumentType template;
        Document digitalGrantDocument;
        registryDetailsService.getRegistryDetails(caseDetails);
        CallbackResponse callbackResponse = CallbackResponse.builder().errors(new ArrayList<>()).build();

        switch (caseData.getCaseType()) {
            case EDGE_CASE_NAME:
                digitalGrantDocument = Document.builder().documentType(DocumentType.EDGE_CASE).build();
                break;
            case INTESTACY_NAME:
                template = INTESTACY_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Intestacy grant document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
            case ADMON_WILL_NAME:
                template = ADMON_WILL_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Admon Will grant document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
            case GRANT_OF_PROBATE_NAME:
            default:
                template = DIGITAL_GRANT;
                digitalGrantDocument = pdfManagementService.generateAndUpload(callbackRequest, template);
                log.info("Generated and Uploaded Grant of Probate document with template {} for the case id {}",
                        template.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());
                break;
        }

        Document coverSheet = pdfManagementService.generateAndUpload(callbackRequest, DocumentType.GRANT_COVER);
        log.info("Generated and Uploaded cover document with template {} for the case id {}",
                DocumentType.GRANT_COVER.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());

        String letterId = null;
        String pdfSize = null;
        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            SendLetterResponse response = bulkPrintService.sendToBulkPrint(callbackRequest, digitalGrantDocument, coverSheet);
            letterId = response != null
                    ? response.letterId.toString()
                    : null;
            callbackResponse = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);

            pdfSize = getPdfSize(caseData);
        }
        if (!callbackResponse.getErrors().isEmpty()) {
            return ResponseEntity.ok(callbackResponse);
        }

        List<Document> documents = new ArrayList<>();
        documents.add(digitalGrantDocument);
        documents.add(coverSheet);

        DocumentType[] documentTypes = {DIGITAL_GRANT_DRAFT, INTESTACY_GRANT_DRAFT, ADMON_WILL_GRANT_DRAFT,
                                        DIGITAL_GRANT_REISSUE_DRAFT, INTESTACY_GRANT_REISSUE_DRAFT,
                                        ADMON_WILL_GRANT_REISSUE_DRAFT};
        for (DocumentType documentType : documentTypes) {
            documentService.expire(callbackRequest, documentType);
        }

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            callbackResponse = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (callbackResponse.getErrors().isEmpty()) {
                Document grantIssuedSentEmail = notificationService.sendEmail(GRANT_ISSUED, caseDetails);
                documents.add(grantIssuedSentEmail);
                callbackResponse = callbackResponseTransformer.addDocuments(callbackRequest, documents, letterId, pdfSize);
            }
        } else {
            callbackResponse = callbackResponseTransformer.addDocuments(callbackRequest, documents, letterId, pdfSize);
        }

        return ResponseEntity.ok(callbackResponse);
    }

    private String getPdfSize(@Valid CaseData caseData) {
        String pdfSize;
        if (caseData.getExtraCopiesOfGrant() != null) {
            pdfSize = String.valueOf(caseData.getExtraCopiesOfGrant() + 2);
        } else {
            pdfSize = String.valueOf(2);
        }
        return pdfSize;
    }


    @PostMapping(path = "/generate-deposit-receipt", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<WillLodgementCallbackResponse> generateDepositReceipt(@RequestBody WillLodgementCallbackRequest callbackRequest) {
        Document document;
        DocumentType template = WILL_LODGEMENT_DEPOSIT_RECEIPT;

        Registry registry = registriesProperties.getRegistries().get(LONDON);
        callbackRequest.getCaseDetails().setLondonRegistryAddress(String.join(" ",
                registry.getAddressLine1(), registry.getAddressLine2(),
                registry.getTown(), registry.getPostcode()));

        document = pdfManagementService.generateAndUpload(callbackRequest, template);

        return ResponseEntity.ok(willLodgementCallbackResponseTransformer.addDocuments(callbackRequest, Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant-draft-reissue", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraftReissue(@RequestBody CallbackRequest callbackRequest) {

        Document document = documentGeneratorService.generateGrantReissue(callbackRequest, DRAFT);

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/generate-grant-reissue", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantReissue(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        List<Document> documents = new ArrayList<>();

        Document grantDocument = documentGeneratorService.generateGrantReissue(callbackRequest, FINAL);
        Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);

        documents.add(grantDocument);
        documents.add(coversheet);

        CaseData caseData = callbackRequest.getCaseDetails().getData();
        String letterId = null;

        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            letterId = bulkPrintService.sendToBulkPrintGrantReissue(callbackRequest, coversheet,
                    grantDocument);
        }

        String pdfSize = getPdfSize(caseData);

        if (caseData.isGrantReissuedEmailNotificationRequested()) {
            documents.add(notificationService.generateGrantReissue(callbackRequest));
        }
        log.info("{} documents generated: {}", documents.size(), documents);
        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                documents, letterId, pdfSize));
    }

    @PostMapping(path = "/generate-sot", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> generateStatementOfTruth(@RequestBody CallbackRequest callbackRequest) {
        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());
        log.info("Initiating call for SoT");
        return ResponseEntity.ok(callbackResponseTransformer.addSOTDocument(callbackRequest,
                documentGeneratorService.generateSoT(callbackRequest)));
    }
}
