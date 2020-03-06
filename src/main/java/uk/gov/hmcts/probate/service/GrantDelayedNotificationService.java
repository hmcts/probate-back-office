package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.GrantDelayedResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.validator.EmailAddressNotifyApplicantValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrantDelayedNotificationService {

    private final NotificationService notificationService;
    private final EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;
    private final CaseQueryService caseQueryService;

    public GrantDelayedResponse handleGrantDelayedNotification(String date) {
        List<String> delayedRepsonseData = new ArrayList<>();
        List<ReturnedCaseDetails> foundCases = caseQueryService.findCasesForGrantDelayed(date);
        log.info("Found cases for grant delayed notification: {}", foundCases.size());
        for (ReturnedCaseDetails foundCase : foundCases) {
            delayedRepsonseData.add(sendNotificationForCase(foundCase));
        }
        return GrantDelayedResponse.builder().delayResponseData(delayedRepsonseData).build();
    }

    private String sendNotificationForCase(ReturnedCaseDetails foundCase) {
        log.info("Preparing to send email to executors for grant delayed notification");
        CCDData dataForEmailAddress = CCDData.builder()
            .primaryApplicantEmailAddress(foundCase.getData().getPrimaryApplicantEmailAddress())
            .applicationType(foundCase.getData().getApplicationType().getCode())
            .build();
        List<FieldErrorResponse> emailErrors = emailAddressNotifyApplicantValidationRule.validate(dataForEmailAddress);
        String caseId = foundCase.getId().toString();
        if (!emailErrors.isEmpty()) {
            log.error("Cannot send Grant Delayed notification, message: {}", emailErrors.get(0).getMessage());
            return getErroredCaseIdentifier(caseId, emailErrors.get(0).getMessage());
        }
        try {
            Document emailDocument = notificationService.sendGrantDelayedEmail(foundCase);
            foundCase.getData().getProbateNotificationsGenerated()
                .add(new CollectionMember<>(null, emailDocument));
            updateFoundCase(foundCase);
        } catch (NotificationClientException e) {
            log.error("Error sending email for Grant Delayed with exception: {}. Has message: {}", e.getClass(), e.getMessage());
            caseId = getErroredCaseIdentifier(caseId, e.getMessage());
        }

        return caseId;
    }

    private String getErroredCaseIdentifier(String caseId, String message) {
        return "<" + caseId + ":" + message +">";
    }

    private void updateFoundCase(ReturnedCaseDetails foundCase) {
        log.info("Updating case for grant delayed, caseId: {}", foundCase.getId());
    }
}
