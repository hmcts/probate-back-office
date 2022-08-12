package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentValidation;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.EvidenceUploadService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistryDetailsService;
import uk.gov.hmcts.probate.service.ReprintService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.NEWCASTLE;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.LATEST_SCHEMA_VERSION;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED_INTESTACY;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.EDGE_CASE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class DocumentController {

    private static final String DRAFT = "preview";
    private static final String FINAL = "final";
    @Autowired
    private final DocumentGeneratorService documentGeneratorService;
    @Autowired
    private final RegistryDetailsService registryDetailsService;
    @Autowired
    private final PDFManagementService pdfManagementService;
    @Autowired
    private final CallbackResponseTransformer callbackResponseTransformer;
    @Autowired
    private final CaseDataTransformer caseDataTransformer;
    @Autowired
    private final WillLodgementCallbackResponseTransformer willLodgementCallbackResponseTransformer;
    @Autowired
    private final NotificationService notificationService;
    @Autowired
    private final RegistriesProperties registriesProperties;
    @Autowired
    private final BulkPrintService bulkPrintService;
    @Autowired
    private final EventValidationService eventValidationService;
    @Autowired
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    @Autowired
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    @Autowired
    private final RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    @Autowired
    private final ReprintService reprintService;
    @Autowired
    private final DocumentValidation documentValidation;
    @Autowired
    private final DocumentManagementService documentManagementService;
    @Autowired
    private final EvidenceUploadService evidenceUploadService;


    private Function<String, State> grantState = (String caseType) -> {
        if (caseType.equals(INTESTACY.getCaseType())) {
            return GRANT_ISSUED_INTESTACY;
        }
        return GRANT_ISSUED;
    };

    @PostMapping(path = "/assembleLetter", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> assembleLetter(
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) {

        CallbackResponse response = callbackResponseTransformer.transformCaseForLetter(callbackRequest);

        return ResponseEntity.ok(response);

    }

    @PostMapping(path = "/previewLetter", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> previewLetter(
        @RequestBody CallbackRequest callbackRequest) {

        Document letterPreview = documentGeneratorService.generateLetter(callbackRequest, false);

        CallbackResponse response =
            callbackResponseTransformer.transformCaseForLetterPreview(callbackRequest, letterPreview);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/generateLetter", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> generateLetter(
        @RequestBody CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        String letterId = null;

        List<Document> documents = new ArrayList<>();
        Document letter = documentGeneratorService.generateLetter(callbackRequest, true);
        Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);

        documents.add(letter);
        documents.add(coversheet);

        if (caseData.isBoAssembleLetterSendToBulkPrintRequested()) {
            letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coversheet,
                letter, true);
        }

        CallbackResponse response =
            callbackResponseTransformer.transformCaseForLetter(callbackRequest, documents, letterId);

        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        registryDetailsService.getRegistryDetails(callbackRequest.getCaseDetails());
        Document document =
            documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.GRANT);

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
            Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(
        @Validated({BulkPrintValidationRule.class})
        @RequestBody CallbackRequest callbackRequest)
        throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        @Valid CaseData caseData = caseDetails.getData();

        registryDetailsService.getRegistryDetails(caseDetails);
        CallbackResponse callbackResponse = CallbackResponse.builder().errors(new ArrayList<>()).build();

        Document digitalGrantDocument = documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL,
            DocumentIssueType.GRANT);

        Document coverSheet = pdfManagementService.generateAndUpload(callbackRequest, DocumentType.GRANT_COVER);
        log.info("Generated and Uploaded cover document with template {} for the case id {}",
            DocumentType.GRANT_COVER.getTemplateName(), callbackRequest.getCaseDetails().getId().toString());

        String letterId = null;
        String pdfSize = null;
        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            SendLetterResponse response =
                bulkPrintService.sendToBulkPrintForGrant(callbackRequest, digitalGrantDocument, coverSheet);
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

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            callbackResponse =
                eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (callbackResponse.getErrors().isEmpty()) {
                Document grantIssuedSentEmail =
                    notificationService.sendEmail(grantState.apply(caseData.getCaseType()), caseDetails);
                documents.add(grantIssuedSentEmail);
                callbackResponse =
                    callbackResponseTransformer.addDocuments(callbackRequest, documents, letterId, pdfSize);
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


    @PostMapping(path = "/generate-deposit-receipt", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WillLodgementCallbackResponse> generateDepositReceipt(
        @RequestBody WillLodgementCallbackRequest callbackRequest) {
        Document document;
        DocumentType template = WILL_LODGEMENT_DEPOSIT_RECEIPT;

        Registry registry = registriesProperties.getRegistries().get(NEWCASTLE);
        callbackRequest.getCaseDetails().setNewcastleRegistryAddress(String.join(" ",
            registry.getAddressLine1(), registry.getAddressLine2(), registry.getAddressLine3(),
            registry.getTown(), registry.getPostcode()));

        document = pdfManagementService.generateAndUpload(callbackRequest, template);

        return ResponseEntity
            .ok(willLodgementCallbackResponseTransformer.addDocuments(callbackRequest, Arrays.asList(document)));
    }

    @PostMapping(path = "/generate-grant-draft-reissue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraftReissue(@RequestBody CallbackRequest callbackRequest) {

        Document document;
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        final CaseData caseData = caseDetails.getData();

        registryDetailsService.getRegistryDetails(caseDetails);


        // The only difference between grant and reissue grant is one statement with the reissue date. Html/pdf template
        // post TC work has lots of complex logic in it that would be difficult to code in Docmosis,
        // so we use html/pdf template instead.
        if (useHtmlPdfGeneratorForReissue(caseData)) {
            document = documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW,
                DocumentIssueType.REISSUE);
        } else {
            document = documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW,
                Optional.of(DocumentIssueType.REISSUE));
        }

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
            Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/generate-grant-reissue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantReissue(@RequestBody CallbackRequest callbackRequest)
        throws NotificationClientException {

        final List<Document> documents = new ArrayList<>();
        Document grantDocument;
        Document coversheet;
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        final CaseData caseData = caseDetails.getData();

        registryDetailsService.getRegistryDetails(caseDetails);

        // The only difference between grant and reissue grant is one statement with the reissue date. Html/pdf template
        // post TC work has lots of complex logic in it that would be difficult to code in Docmosis,
        // so we use html/pdf template instead.
        if (useHtmlPdfGeneratorForReissue(caseData)) {
            grantDocument = documentGeneratorService.getDocument(callbackRequest, DocumentStatus.FINAL,
                DocumentIssueType.REISSUE);
            coversheet = pdfManagementService.generateAndUpload(callbackRequest, DocumentType.GRANT_COVER);
        } else {
            grantDocument = documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL,
                Optional.of(DocumentIssueType.REISSUE));
            coversheet = documentGeneratorService.generateCoversheet(callbackRequest);
        }

        documents.add(grantDocument);
        documents.add(coversheet);

        String letterId = null;

        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coversheet,
                grantDocument, true);
        }

        String pdfSize = getPdfSize(caseData);

        if (caseData.isGrantReissuedEmailNotificationRequested()) {
            documents.add(notificationService.generateGrantReissue(callbackRequest));
        }
        log.info("{} documents generated: {}", documents.size(), documents);
        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
            documents, letterId, pdfSize));
    }

    // This only seems to be called once list lists are mapped to exec lists
    @PostMapping(path = "/generate-sot", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateStatementOfTruth(@RequestBody CallbackRequest callbackRequest) {
        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());

        log.info("Initiating call for SoT");
        caseDataTransformer.transformCaseDataForLegalStatementRegeneration(callbackRequest);
        return ResponseEntity.ok(callbackResponseTransformer.addSOTDocument(callbackRequest,
            documentGeneratorService.generateSoT(callbackRequest)));
    }

    @PostMapping(path = "/default-reprint-values", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> defaultReprintValues(@RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(callbackResponseTransformer.transformCaseForReprint(callbackRequest));
    }

    @PostMapping(path = "/reprint", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> reprint(@RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(reprintService.reprintSelectedDocument(callbackRequest));
    }

    @PostMapping(path = "/uploadDocument", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> uploadDocument(@RequestBody CallbackRequest callbackRequest) {
        evidenceUploadService.updateLastEvidenceAddedDate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);
        return ResponseEntity.ok(response);
    }

    private boolean useHtmlPdfGeneratorForReissue(CaseData cd) {
        if ((GRANT_TYPE_PROBATE.equals(cd.getSolsWillType()) || GRANT_OF_PROBATE_NAME.equals(cd.getCaseType()))
            && (cd.getApplicationType() == null || SOLICITOR.equals(cd.getApplicationType()))
            && (LATEST_SCHEMA_VERSION.equals(cd.getSchemaVersion()))) {

            return true;
        }
        return false;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<String> upload(
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestHeader(value = "ServiceAuthorization") String serviceAuthorizationToken,
        @RequestPart("file") List<MultipartFile> files
    ) {
        List<String> result = new ArrayList<>();
        List<String> fileValidationErrors = documentValidation.validateFiles(files);
        if (!fileValidationErrors.isEmpty()) {
            result.addAll(fileValidationErrors);
            return result;
        }

        log.info("Uploading document at BackOffice");
        UploadResponse uploadResponse = documentManagementService
            .uploadForCitizen(files, authorizationToken, DocumentType.DIGITAL_GRANT);
        if (uploadResponse != null) {
            result = uploadResponse
                .getDocuments()
                .stream()
                .map(f -> f.links.self.href)
                .collect(Collectors.toList());
        }

        return result;
    }

}
