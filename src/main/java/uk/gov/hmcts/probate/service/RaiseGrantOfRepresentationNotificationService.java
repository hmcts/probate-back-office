package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.GRANT_RAISED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaiseGrantOfRepresentationNotificationService {

    private final NotificationService notificationService;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final EventValidationService eventValidationService;
    private final List<EmailAddressNotifyValidationRule> emailAddressNotifyValidationRules;

    public CallbackResponse handleGrantReceivedNotification(CallbackRequest callbackRequest,
                                                            Optional<UserInfo> caseworkerInfo)
        throws NotificationClientException {

        log.info("Preparing to send notifications for raising a grant application.");
        CallbackResponse response = CallbackResponse.builder().errors(new ArrayList<>()).build();
        List<Document> documents = new ArrayList<>();
        String letterId = null;
        boolean useEmailNotification =
            callbackRequest.getCaseDetails().getData().getDefaultValueForEmailNotifications().equals(YES);

        if (useEmailNotification) {
            log.info("Email address available, sending email to applicant.");
            response =
                eventValidationService.validateEmailRequest(callbackRequest, emailAddressNotifyValidationRules);
            if (response.getErrors().isEmpty()) {
                Document document = notificationService.sendEmail(GRANT_RAISED, callbackRequest.getCaseDetails());
                documents.add(document);
                log.info("Adding document {}", document);
            }
        } else {
            log.info("Email address not available and letter sending not currently available.");
        }

        if (response.getErrors().isEmpty()) {
            response = callbackResponseTransformer.grantRaised(callbackRequest, documents, letterId, caseworkerInfo);
        }
        return response;
    }

}
