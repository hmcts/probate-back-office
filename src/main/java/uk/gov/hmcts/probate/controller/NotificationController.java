package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;

@RequiredArgsConstructor
@RequestMapping(value = "/notify", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;

    @PostMapping(path = "/documents-received")
    public ResponseEntity<CallbackResponse> sendDocumentReceivedNotification(
            @Validated({EmailAddressNotificationValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        CallbackResponse response;

        List<Document> documents = new ArrayList<>();
        if (caseData.isDocsReceivedEmailNotificationRequested()) {
            response = eventValidationService.validateRequest(callbackRequest, emailAddressNotificationValidationRules);
            if (response.getErrors().isEmpty()) {
                Document documentsReceivedSentEmail = notificationService.sendEmail(DOCUMENTS_RECEIVED, caseDetails);
                documents.add(documentsReceivedSentEmail);
                response = callbackResponseTransformer.addDocuments(callbackRequest, documents);
            }
        } else {
            response = callbackResponseTransformer.addDocuments(callbackRequest, documents);

        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/case-stopped")
    public ResponseEntity<CallbackResponse> sendCaseStoppedNotification(
            @Validated({EmailAddressNotifyValidationRule.class})
            @RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {
        CallbackResponse response;
        Document document;
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        response = eventValidationService.validateRequest(callbackRequest, emailAddressNotifyValidationRules);
        if (response.getErrors().isEmpty()) {
            document = notificationService.sendEmail(CASE_STOPPED, caseDetails);
            response = callbackResponseTransformer.caseStopped(callbackRequest, document);
        }
        return ResponseEntity.ok(response);
    }


}
