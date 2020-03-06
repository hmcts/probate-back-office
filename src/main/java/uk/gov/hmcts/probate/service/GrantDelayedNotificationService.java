package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrantDelayedNotificationService {

    private final NotificationService notificationService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private final CaseQueryService caseQueryService;

    public String handleGrantDelayedNotification(String date) {
        String processedCases = "";
        List<ReturnedCaseDetails> foundCases = caseQueryService.findCasesForGrantDelayed(date);
        log.info("Found cases for grant delayed notification: {}", foundCases.size());
        for (ReturnedCaseDetails foundCase : foundCases) {
            processedCases += "," + sendNotificationForCase(foundCase);
        }
        return processedCases;
    }

    private String sendNotificationForCase(ReturnedCaseDetails foundCase) {
        log.info("Preparing to send email to executors for grant delayed notification");
        CCDData dataForEmailAddress = CCDData.builder().primaryApplicantEmailAddress(foundCase.getData().getPrimaryApplicantEmailAddress()).build();
        List<FieldErrorResponse> emailErrors = emailAddressNotifyApplicantValidationRule.validate(dataForEmailAddress);
        if (!emailErrors.isEmpty()) {
            return "<" + emailErrors.get(0).getMessage() + ">";
        }
        String caseId = foundCase.getId().toString();
        try {
            Document emailDocument = notificationService.sendGrantDelayedEmail(foundCase);
            foundCase.getData().getProbateNotificationsGenerated()
                .add(new CollectionMember<>(null, emailDocument));
            updateFoundCase(foundCase);
        } catch (NotificationClientException e) {
            log.error("Error sending email for Grant Delayed with exception: {}. Has message: {}", e.getClass(), e.getMessage());
            caseId = "*" + caseId + "*";
        }

        return caseId;
    }

    private void updateFoundCase(ReturnedCaseDetails foundCase) {
        log.info("Updating case for grant delayed, caseId: {}", foundCase.getId());
    }
}
