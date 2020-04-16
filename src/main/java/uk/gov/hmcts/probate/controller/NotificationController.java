package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentsReceivedNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RaiseGrantOfRepresentationNotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentType;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;

@RequiredArgsConstructor
@RequestMapping(value = "/notify", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class NotificationController {

    @Autowired
    private final DocumentGeneratorService documentGeneratorService;
    private final DocumentsReceivedNotificationService documentsReceivedNotificationService;
    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final GrantOfRepresentationDocmosisMapperService gorDocmosisService;
    private final InformationRequestService informationRequestService;
    private final RedeclarationNotificationService redeclarationNotificationService;
    private final RaiseGrantOfRepresentationNotificationService raiseGrantOfRepresentationNotificationService;
    private final ObjectMapper objectMapper;
    private static final String DEFAULT_LOG_ERROR = "Case Id: {} ERROR: {}";
    private static final String INVALID_PAYLOAD = "Invalid payload";
    private final GrantNotificationService grantNotificationService;

    @PostMapping(path = "/application-received")
    public ResponseEntity<ProbateDocument> sendApplicationReceivedNotification(
        @Validated({EmailAddressNotificationValidationRule.class})
        @RequestBody CallbackRequest callbackRequest)
        throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (isAnEmailAddressPresent(caseData)) {
            CallbackResponse response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (response.getErrors().isEmpty()) {
                Document sentEmailAsDocument = notificationService.sendEmail(APPLICATION_RECEIVED, caseDetails);
                return ResponseEntity.ok(buildProbateDocument(sentEmailAsDocument));
            }
        }

        log.info("No email sent or document returned to case: {}", caseDetails.getId());
        return ResponseEntity.ok(null);
    }

    @PostMapping(path = "/case-stopped")
    public ResponseEntity<CallbackResponse> sendCaseStoppedNotification(
            @Validated({EmailAddressNotifyValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        CallbackResponse response = CallbackResponse.builder().errors(new ArrayList<>()).build();

        Document document;
        List<Document> documents = new ArrayList<>();
        String letterId = null;

        if (caseData.isCaveatStopNotificationRequested() && caseData.isCaveatStopEmailNotificationRequested()) {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                log.info("Initiate call to send caveat email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                document = notificationService.sendEmail(CASE_STOPPED_CAVEAT, caseDetails);
                documents.add(document);
                log.info("Successful response for caveat email for case id {} ",
                        callbackRequest.getCaseDetails().getId());
            }
        } else if (caseData.isCaveatStopNotificationRequested() && !caseData.isCaveatStopEmailNotificationRequested()) {

            Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);
            documents.add(coversheet);

            log.info("Initiate call to generate Caveat stopped document for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            Map<String, Object> placeholders = gorDocmosisService.caseDataForStoppedMatchedCaveat(callbackRequest.getCaseDetails());
            Document caveatRaisedDoc =
                    pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType
                            .CAVEAT_STOPPED);
            documents.add(caveatRaisedDoc);
            log.info("Successful response for caveat stopped document for case id {} ", callbackRequest.getCaseDetails().getId());

            if (caseData.isCaveatStopSendToBulkPrintRequested()) {
                log.info("Initiate call to bulk print for Caveat stopped document and coversheet for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                SendLetterResponse sendLetterResponse =
                    bulkPrintService.sendToBulkPrintForGrant(callbackRequest, caveatRaisedDoc, null, coversheet);
                letterId = sendLetterResponse != null
                        ? sendLetterResponse.letterId.toString()
                        : null;
                response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
            }
        } else {
            response = eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                log.info("Initiate call to notify applicant for case id {} ",
                        callbackRequest.getCaseDetails().getId());
                document = notificationService.sendEmail(CASE_STOPPED, caseDetails);
                documents.add(document);
                log.info("Successful response from notify for case id {} ",
                        callbackRequest.getCaseDetails().getId());
            }
        }
        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.caseStopped(callbackRequest, documents, letterId);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/request-information-default-values")
    public ResponseEntity<CallbackResponse> requestInformationDefaultValues(
            @RequestBody CallbackRequest callbackRequest) {
        CallbackResponse callbackResponse = callbackResponseTransformer.defaultRequestInformationValues(callbackRequest);
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/documents-received")
    public ResponseEntity<CallbackResponse> sendDocumentReceivedNotification(
            @Validated({EmailAddressNotificationValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {
        return ResponseEntity.ok(documentsReceivedNotificationService.handleDocumentReceivedNotification(callbackRequest));
    }

    @PostMapping(path = "/stopped-information-request")
    public ResponseEntity<CallbackResponse> informationRequest(@RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(informationRequestService.handleInformationRequest(callbackRequest));
    }

    @PostMapping(path = "/redeclaration-sot")
    public ResponseEntity<CallbackResponse> redeclarationSot(@RequestBody CallbackRequest callbackRequest) {
        return ResponseEntity.ok(redeclarationNotificationService.handleRedeclarationNotification(callbackRequest));
    }

    @PostMapping(path = "/grant-received")
    public ResponseEntity<CallbackResponse> sendGrantReceivedNotification(
            @Validated({EmailAddressNotificationValidationRule.class})
            @RequestBody CallbackRequest callbackRequest) throws NotificationClientException {
        return ResponseEntity.ok(raiseGrantOfRepresentationNotificationService.handleGrantReceivedNotification(callbackRequest));
    }

    private ProbateDocument buildProbateDocument(Document boDocument) {
        ProbateDocumentLink probateDocumentLink = ProbateDocumentLink.builder()
            .documentBinaryUrl(boDocument.getDocumentLink().getDocumentBinaryUrl())
            .documentFilename(boDocument.getDocumentLink().getDocumentFilename())
            .documentUrl(boDocument.getDocumentLink().getDocumentUrl())
            .build();
        ProbateDocumentType probateDocumentType = ProbateDocumentType.valueOf(boDocument.getDocumentType().name());
        return ProbateDocument.builder()
            .documentDateAdded(boDocument.getDocumentDateAdded())
            .documentFileName(boDocument.getDocumentFileName())
            .documentGeneratedBy(boDocument.getDocumentGeneratedBy())
            .documentLink(probateDocumentLink)
            .documentType(probateDocumentType)
            .build();

    }

    private boolean isAnEmailAddressPresent(CaseData caseData) {
        return caseData.isDocsReceivedEmailNotificationRequested();
    }

    @PostMapping(path = "/start-grant-delayed-notify-period", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> startDelayedNotificationPeriod(
            @RequestBody CallbackRequest callbackRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        logRequest(request.getRequestURI(), callbackRequest);
        log.info("start-delayed-notify-period started");
        notificationService.startGrantDelayNotificationPeriod(callbackRequest.getCaseDetails());
        notificationService.resetAwaitingDocumentationNotificationDate(callbackRequest.getCaseDetails());
        CallbackResponse response = callbackResponseTransformer.transformCase(callbackRequest);
        return ResponseEntity.ok(response);
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            log.info("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
            if (log.isDebugEnabled()) {
                log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
            }
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }

    @PostMapping(path = "/grant-delayed-scheduled")
    public ResponseEntity<GrantScheduleResponse> grantDelayed(@RequestParam("date") final String date) {
        GrantScheduleResponse grantScheduleResponse = grantNotificationService.handleGrantDelayedNotification(date);
        log.info("Grants delayed attempted for: {} grants", grantScheduleResponse.getScheduleResponseData().size());
        return ResponseEntity.ok(grantScheduleResponse);
    }

    @PostMapping(path = "/grant-awaiting-documents-scheduled")
    public ResponseEntity<GrantScheduleResponse> grantAwaitingDocuments(@RequestParam("date") final String date) {
        GrantScheduleResponse grantScheduleResponse = grantNotificationService.handleAwaitingDocumentationNotification(date);
        log.info("Grants delayed attempted for: {} grants", grantScheduleResponse.getScheduleResponseData().size());
        return ResponseEntity.ok(grantScheduleResponse);
    }

}
