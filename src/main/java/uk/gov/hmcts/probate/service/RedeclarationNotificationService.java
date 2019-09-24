package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.State;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedeclarationNotificationService {

    private final NotificationService notificationService;
    private final EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;
    private final CallbackResponseTransformer callbackResponseTransformer;

    public CallbackResponse handleRedeclarationNotification(CallbackRequest callbackRequest) {

        log.info("Preparing to send email to executors for redeclaration notification");
        emailAddressExecutorsApplyingValidationRule.validate(callbackRequest.getCaseDetails());

        List<Document> emailDocument = new ArrayList<>();

        for (CollectionMember<ExecutorsApplyingNotification> executor :
                callbackRequest.getCaseDetails().getData().getExecutorsApplyingNotifications()) {
            if (YES.equals(executor.getValue().getNotification())) {
                try {
                    emailDocument.add(
                            notificationService.sendEmailWithDocumentAttached(
                                    callbackRequest.getCaseDetails(), executor.getValue(), State.REDECLARATION_SOT));
                } catch (NotificationClientException | IOException e) {
                    log.error("Error sending email with exception: {}. Has message: {}", e.getClass(), e.getMessage());
                }
            }
        }
        return callbackResponseTransformer.addDocuments(callbackRequest, emailDocument, null, null);
    }
}
