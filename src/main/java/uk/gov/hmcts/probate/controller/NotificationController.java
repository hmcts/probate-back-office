package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.GrantScheduleResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.ScannedDocumentOrderingService;
import uk.gov.hmcts.probate.service.DocumentsReceivedNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.EvidenceUploadService;
import uk.gov.hmcts.probate.service.GrantNotificationService;
import uk.gov.hmcts.probate.service.InformationRequestService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RaiseGrantOfRepresentationNotificationService;
import uk.gov.hmcts.probate.service.RedeclarationNotificationService;
import uk.gov.hmcts.probate.service.docmosis.GrantOfRepresentationDocmosisMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.service.user.UserInfoService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.transformer.HandOffLegacyTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;
import uk.gov.service.notify.NotificationClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_BULKSCAN;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED_NO_DOCS;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.NOC;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.Constants.CASE_PRINTED_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@RequiredArgsConstructor
@RequestMapping(value = "/notify", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class NotificationController {

    private static final String DEFAULT_LOG_ERROR = "Case Id: {} ERROR: {}";
    private static final String INVALID_PAYLOAD = "Invalid payload";
    @Autowired
    private final DocumentGeneratorService documentGeneratorService;
    private final ScannedDocumentOrderingService scannedDocumentOrderingService;
    private final DocumentsReceivedNotificationService documentsReceivedNotificationService;
    private final NotificationService notificationService;
    private final EvidenceUploadService evidenceUploadService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;
    private final PDFManagementService pdfManagementService;
    private final BulkPrintService bulkPrintService;
    private final List<BulkPrintValidationRule> bulkPrintValidationRules;
    private final GrantOfRepresentationDocmosisMapperService gorDocmosisService;
    private final InformationRequestService informationRequestService;
    private final RedeclarationNotificationService redeclarationNotificationService;
    private final RaiseGrantOfRepresentationNotificationService raiseGrantOfRepresentationNotificationService;
    private final ObjectMapper objectMapper;
    private final GrantNotificationService grantNotificationService;
    private final CaseDataTransformer caseDataTransformer;
    private final HandOffLegacyTransformer handOffLegacyTransformer;
    private final NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule;
    private final UserInfoService userInfoService;

    @PostMapping(path = "/application-received")
    public ResponseEntity<ProbateDocument> sendApplicationReceivedNotification(
        @RequestBody CallbackRequest callbackRequest)
        throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (isDigitalApplication(caseData) && isAnEmailAddressPresent(caseData)) {
            CallbackResponse response =
                eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                Document sentEmailAsDocument;
                if (YES.equals(caseData.getPrimaryApplicantNotRequiredToSendDocuments())
                        && INTESTACY_NAME.equals(caseData.getCaseType())) {
                    sentEmailAsDocument = notificationService.sendEmail(APPLICATION_RECEIVED_NO_DOCS, caseDetails);
                } else {
                    notificationService.startAwaitingDocumentationNotificationPeriod(callbackRequest.getCaseDetails());
                    sentEmailAsDocument = notificationService.sendEmail(APPLICATION_RECEIVED, caseDetails);
                }
                return ResponseEntity.ok(sentEmailAsDocument.asProbateDocument());
            }
        }

        log.info("No email sent or document returned to case: {}", caseDetails.getId());
        return ResponseEntity.ok(null);
    }

    @PostMapping(path = "/case-stopped")
    public ResponseEntity<CallbackResponse> sendCaseStoppedNotification(
        @RequestHeader(value = "Authorization") String authToken,
        @Validated({EmailAddressNotifyValidationRule.class})
        @RequestBody CallbackRequest callbackRequest)
        throws NotificationClientException {


        Document document;
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CallbackResponse response =
                eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
        if (response.getErrors().isEmpty()) {
            log.info("Initiate call to send caveat email for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            document = notificationService.sendEmail(CASE_STOPPED_CAVEAT, caseDetails);
            documents.add(document);
            log.info("Successful response for caveat email for case id {} ", callbackRequest.getCaseDetails().getId());
        } else {
            Document coversheet = documentGeneratorService.generateCoversheet(callbackRequest);
            documents.add(coversheet);

            log.info("Initiate call to generate Caveat stopped document for case id {} ",
                callbackRequest.getCaseDetails().getId());
            Map<String, Object> placeholders =
                gorDocmosisService.caseDataForStoppedMatchedCaveat(callbackRequest.getCaseDetails());
            Document caveatRaisedDoc =
                pdfManagementService.generateDocmosisDocumentAndUpload(placeholders, DocumentType
                    .CAVEAT_STOPPED);
            documents.add(caveatRaisedDoc);
            log.info("Successful response for caveat stopped document for case id {} ",
                callbackRequest.getCaseDetails().getId());

            log.info("Initiate call to bulk print for Caveat stopped document and coversheet for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            SendLetterResponse sendLetterResponse =
                    bulkPrintService.sendToBulkPrintForGrant(callbackRequest, caveatRaisedDoc, coversheet);
            letterId = sendLetterResponse != null
                    ? sendLetterResponse.letterId.toString() : null;
            response = eventValidationService.validateBulkPrintResponse(letterId, bulkPrintValidationRules);
        }
        if (response.getErrors().isEmpty()) {
            Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
            response = callbackResponseTransformer.caseStopped(callbackRequest, documents, letterId, caseworkerInfo);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/redeclaration-sot-default-values")
    public ResponseEntity<CallbackResponse> redeclarationSOTDefaultValues(
        @RequestBody CallbackRequest callbackRequest) {
        CallbackResponse callbackResponse =
            callbackResponseTransformer.defaultRedeclarationSOTValues(callbackRequest);
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/request-information-default-values")
    public ResponseEntity<CallbackResponse> requestInformationDefaultValues(
            @RequestBody CallbackRequest callbackRequest) {
        CallbackResponse callbackResponse =
                callbackResponseTransformer.defaultRequestInformationValues(callbackRequest);
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/documents-received")
    public ResponseEntity<CallbackResponse> sendDocumentReceivedNotification(
            @RequestBody CallbackRequest callbackRequest) throws NotificationClientException {
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(documentsReceivedNotificationService
                .handleDocumentReceivedNotification(callbackRequest, caseworkerInfo));
    }

    @PostMapping(path = "/stopped-information-request")
    public ResponseEntity<CallbackResponse> informationRequest(
            @RequestBody final CallbackRequest callbackRequest) throws NotificationClientException {
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        log.info("caseworker info: {}", caseworkerInfo.orElse(null));
        CallbackResponse response = informationRequestService.handleInformationRequest(callbackRequest, caseworkerInfo);

        if (response.getErrors().stream().anyMatch(error ->
                error.contains("Status code: 400")
                        && error.contains("\"message\":\"email_address Not a valid email address\""))) {
            log.warn("Invalid applicant email detected for case id: {}", callbackRequest.getCaseDetails().getId());
            if (caseworkerInfo.isPresent()) {
                log.info("Sending email to caseworker about invalid applicant email for case id: {}",
                        callbackRequest.getCaseDetails().getId());
                log.info("Email sent successfully to caseworker for case id: {}",
                        callbackRequest.getCaseDetails().getId());
            } else {
                log.warn("Caseworker info unavailable. Unable to notify caseworker for case id: {}",
                        callbackRequest.getCaseDetails().getId());
            }
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/information-request-email-preview")
    public ResponseEntity<CallbackResponse> emailPreview(@RequestBody CallbackRequest callbackRequest) {
        Document document = informationRequestService.emailPreview(callbackRequest);
        return ResponseEntity.ok(callbackResponseTransformer.addDocumentPreview(callbackRequest, document));
    }

    @PostMapping(path = "/redeclaration-sot")
    public ResponseEntity<CallbackResponse> redeclarationSot(@RequestBody CallbackRequest callbackRequest) {
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity.ok(redeclarationNotificationService.handleRedeclarationNotification(callbackRequest,
                caseworkerInfo));
    }

    @PostMapping(path = "/grant-received")
    public ResponseEntity<CallbackResponse> sendGrantReceivedNotification(
            @RequestBody CallbackRequest callbackRequest) throws NotificationClientException {
        caseDataTransformer.transformCaseDataForEvidenceHandledForCreateBulkscan(callbackRequest);
        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData());
        handOffLegacyTransformer.setHandOffToLegacySiteYes(callbackRequest);
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        return ResponseEntity
            .ok(raiseGrantOfRepresentationNotificationService.handleGrantReceivedNotification(callbackRequest,
                    caseworkerInfo));
    }

    @PostMapping(path = "/start-grant-delayed-notify-period", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> startDelayedNotificationPeriod(
        @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult,
        HttpServletRequest request) throws NotificationClientException {
        logRequest(request.getRequestURI(), callbackRequest);
        notificationService.startGrantDelayNotificationPeriod(callbackRequest.getCaseDetails());
        notificationService.resetAwaitingDocumentationNotificationDate(callbackRequest.getCaseDetails());
        caseDataTransformer.transformCaseDataForAttachDocuments(callbackRequest);
        evidenceUploadService.updateLastEvidenceAddedDate(callbackRequest.getCaseDetails());
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        scannedDocumentOrderingService.orderScannedDocuments(caseData);
        Document document = null;
        if (isAnEmailAddressPresent(caseData)
            && eventValidationService
                .validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules).getErrors().isEmpty()
            && (CASE_PRINTED_NAME.equals(callbackRequest.getCaseDetails().getState()))) {
            document = notificationService.sendEmail(DOCUMENTS_RECEIVED, callbackRequest.getCaseDetails());
            caseDataTransformer.transformCaseDataForDocsReceivedNotificationSent(callbackRequest);
        }
        Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
        CallbackResponse response = callbackResponseTransformer
                .transformCaseForAttachScannedDocs(callbackRequest, document, caseworkerInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/grant-delayed-scheduled")
    public ResponseEntity<GrantScheduleResponse> grantDelayed(@RequestParam("date") final String date) {
        log.info("Calling perform Grants delayed...");
        GrantScheduleResponse grantScheduleResponse = grantNotificationService.handleGrantDelayedNotification(date);
        log.info("Grants delayed attempted for: {} grants, {}", grantScheduleResponse.getScheduleResponseData().size(),
            StringUtils.joinWith(",", grantScheduleResponse.getScheduleResponseData()));
        log.info("...Called perform Grants delayed");
        return ResponseEntity.ok(grantScheduleResponse);
    }

    @PostMapping(path = "/grant-awaiting-documents-scheduled")
    public ResponseEntity<GrantScheduleResponse> grantAwaitingDocuments(@RequestParam("date") final String date) {
        log.info("Calling perform Grants Awaiting Documents...");
        GrantScheduleResponse grantScheduleResponse =
            grantNotificationService.handleAwaitingDocumentationNotification(date);
        log.info("Grants awaiting documents attempted for: {} grants, {}",
            grantScheduleResponse.getScheduleResponseData().size(),
            StringUtils.joinWith(",", grantScheduleResponse.getScheduleResponseData()));
        log.info("...Called perform Grants Awaiting Documents");
        return ResponseEntity.ok(grantScheduleResponse);

    }

    @PostMapping(path = "/noc-notification")
    public ResponseEntity<CallbackResponse> sendNOCEmailNotification(
            @RequestBody CallbackRequest callbackRequest) throws NotificationClientException {
        log.info("Preparing to send email notification for NOC");
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CallbackResponse response;

        List<Document> documents = new ArrayList<>();
        response = eventValidationService.validateNocEmail(caseData, nocEmailAddressNotifyValidationRule);
        if (response.getErrors().isEmpty() && !isFirstNOCOnBulkScan(caseData)) {
            log.info("Initiate call to notify Solicitor for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            Document nocSentEmail = notificationService.sendNocEmail(NOC, caseDetails);
            documents.add(nocSentEmail);
            log.info("Successful response from notify for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            Optional<UserInfo> caseworkerInfo = userInfoService.getCaseworkerInfo();
            response = callbackResponseTransformer.addNocDocuments(callbackRequest, documents, caseworkerInfo);
        } else {
            log.info("No email sent or document returned to {} case: {}",
                    caseData.getChannelChoice(), caseDetails.getId());
        }
        return ResponseEntity.ok(response);
    }

    private boolean isAnEmailAddressPresent(CaseData caseData) {
        return caseData.isDocsReceivedEmailNotificationRequested();
    }

    private void logRequest(String uri, CallbackRequest callbackRequest) {
        try {
            log.info("POST: {} Case Id: {} ", uri, callbackRequest.getCaseDetails().getId().toString());
            if (log.isDebugEnabled()) {
                log.debug("POST: {} {}", uri, objectMapper.writeValueAsString(callbackRequest));
            }
        } catch (JsonProcessingException e) {
            log.error("POST: {}", uri, e);
        }
    }

    private boolean isDigitalApplication(CaseData caseData) {
        return !YES.equalsIgnoreCase(caseData.getPaperForm());
    }

    private boolean isFirstNOCOnBulkScan(CaseData caseData) {
        return CHANNEL_CHOICE_BULKSCAN.equals(caseData.getChannelChoice())
                && caseData.getChangeOfRepresentatives().size() == 1;
    }
}
