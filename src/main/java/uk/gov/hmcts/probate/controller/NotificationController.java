package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.service.notify.NotificationClientException;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;

@RequiredArgsConstructor
@RequestMapping(value = "/notify", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;

    @PostMapping(path = "/documents-received")
    public ResponseEntity<CallbackResponse> sendDocumentReceivedNotification(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (caseData.isDocsReceivedEmailNotificationRequested()) {
            notificationService.sendEmail(DOCUMENTS_RECEIVED, caseData);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addCcdState(callbackRequest));
    }

    @PostMapping(path = "/grant-issued")
    public ResponseEntity<CallbackResponse> sendGrantIssuedNotification(@RequestBody CallbackRequest callbackRequest)
            throws NotificationClientException {

        CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (caseData.isGrantIssuedEmailNotificationRequested()) {
            notificationService.sendEmail(GRANT_ISSUED, caseData);
        }

        return ResponseEntity.ok(callbackResponseTransformer.addCcdState(callbackRequest));
    }
}
