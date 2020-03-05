package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.EmailAddressExecutorsApplyingValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrantDelayedNotificationService {

    private final NotificationService notificationService;
    private final EmailAddressExecutorsApplyingValidationRule emailAddressExecutorsApplyingValidationRule;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaseQueryService caseQueryService;
    private final CoreCaseDataService coreCaseDataService;
    private final SecurityUtils securityUtils;

    public String handleGrantDelayedNotification(String date) {
        String processedCases = "";
        List<ReturnedCaseDetails> foundCases = caseQueryService.findCasesForGrantDelayed(date);
        for (ReturnedCaseDetails foundCase : foundCases) {
            processedCases += sendNotificationForCase(foundCase);
        }
        return processedCases;
    }

    private String sendNotificationForCase(ReturnedCaseDetails foundCase) {
        log.info("Preparing to send email to executors for grant delayed notification");
        //emailAddressExecutorsApplyingValidationRule.validate(foundCase.getData());
        String caseId = foundCase.getId().toString();
        try {
            Document emailDocument = notificationService.sendGrantDelayedEmail(foundCase);
            foundCase.getData().getProbateNotificationsGenerated()
                .add(new CollectionMember<>(null, emailDocument));
            updateFoundCase(foundCase);
        } catch (NotificationClientException e) {
            log.error("Error sending email with exception: {}. Has message: {}", e.getClass(), e.getMessage());
            caseId = "*" + caseId + "*";
        }

        return caseId;
    }

    private void updateFoundCase(ReturnedCaseDetails foundCase) {
        log.info("Updating case for grant delayed, caseId: {}", foundCase.getId());
    }
}
