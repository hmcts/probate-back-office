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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import uk.gov.hmcts.probate.service.DocumentService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.FindWillsService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistryDetailsService;
import uk.gov.hmcts.probate.service.ReprintService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.LONDON;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED_INTESTACY;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.EDGE_CASE_NAME;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/document", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
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
    private final ReprintService reprintService;
    private final FindWillsService findWillService;
    private static final String DRAFT = "preview";
    private static final String FINAL = "final";

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

        CallbackResponse response = callbackResponseTransformer.transformCaseForLetterPreview(callbackRequest, letterPreview);

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
                    letter, Collections.EMPTY_LIST, true);
        }

        CallbackResponse response = callbackResponseTransformer.transformCaseForLetter(callbackRequest, documents, letterId);

        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/generate-grant-draft", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraft(@RequestBody CallbackRequest callbackRequest) {
        registryDetailsService.getRegistryDetails(callbackRequest.getCaseDetails());
        Document document = documentGeneratorService.getDocument(callbackRequest, DocumentStatus.PREVIEW, DocumentIssueType.GRANT);

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/determine-wills-available", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> determineWillsAvailable(
        @Validated({EmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
        @RequestBody CallbackRequest callbackRequest) {

        CallbackResponse callbackResponse = callbackResponseTransformer.transformCaseWillList(callbackRequest);
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/generate-grant", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrant(
            @Validated({EmailAddressNotificationValidationRule.class, BulkPrintValidationRule.class})
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

        List<Document> willDocuments = findWillService.findWills(caseData);
        log.info("number of willDocuments found on case: {}", willDocuments.size());

        String letterId = null;
        String pdfSize = null;
        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            SendLetterResponse response = bulkPrintService.sendToBulkPrintForGrant(callbackRequest, digitalGrantDocument, coverSheet, willDocuments);
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
            callbackResponse = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (callbackResponse.getErrors().isEmpty()) {
                Document grantIssuedSentEmail = notificationService.sendEmail(grantState.apply(caseData.getCaseType()), caseDetails);
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


    @PostMapping(path = "/generate-deposit-receipt", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(path = "/generate-grant-draft-reissue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantDraftReissue(@RequestBody CallbackRequest callbackRequest) {

        Document document = documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.PREVIEW,
                Optional.of(DocumentIssueType.REISSUE));

        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                Arrays.asList(document), null, null));
    }

    @PostMapping(path = "/generate-grant-reissue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateGrantReissue(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        List<Document> documents = new ArrayList<>();

        Document grantDocument = documentGeneratorService.generateGrantReissue(callbackRequest, DocumentStatus.FINAL,
                Optional.of(DocumentIssueType.REISSUE));
        Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);

        documents.add(grantDocument);
        documents.add(coversheet);

        CaseData caseData = callbackRequest.getCaseDetails().getData();
        String letterId = null;

        if (caseData.isSendForBulkPrintingRequested() && !EDGE_CASE_NAME.equals(caseData.getCaseType())) {
            letterId = bulkPrintService.optionallySendToBulkPrint(callbackRequest, coversheet,
                    grantDocument, Collections.EMPTY_LIST, true);
        }

        String pdfSize = getPdfSize(caseData);

        if (caseData.isGrantReissuedEmailNotificationRequested()) {
            documents.add(notificationService.generateGrantReissue(callbackRequest));
        }
        log.info("{} documents generated: {}", documents.size(), documents);
        return ResponseEntity.ok(callbackResponseTransformer.addDocuments(callbackRequest,
                documents, letterId, pdfSize));
    }

    @PostMapping(path = "/generate-sot", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> generateStatementOfTruth(@RequestBody CallbackRequest callbackRequest) {
        redeclarationSoTValidationRule.validate(callbackRequest.getCaseDetails());
        log.info("Initiating call for SoT");
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
}
