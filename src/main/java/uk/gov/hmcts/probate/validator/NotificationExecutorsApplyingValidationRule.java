package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@RequiredArgsConstructor
public class NotificationExecutorsApplyingValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String SEND_NOTIFICATION_EMPTY = "sendNotificationEmpty";

    @Override
    public void validate(CaseDetails caseDetails) {
        int counter = 0;

        String[] args = {caseDetails.getId().toString()};
        CaseData caseData = caseDetails.getData();
        String userMessage = businessValidationMessageRetriever.getMessage(SEND_NOTIFICATION_EMPTY, args, Locale.UK);

        for (CollectionMember<ExecutorsApplyingNotification> executor : caseData.getExecutorsApplyingNotifications()){
            if (executor.getValue().getNotification().equals(NO)) {
                counter++;
            }
        }
        if (counter == caseData.getExecutorsApplyingNotifications().size()) {
            throw new BusinessValidationException(userMessage,
                    "No applicant selected to send notification for case id " + caseDetails.getId());
        }
    }
}
