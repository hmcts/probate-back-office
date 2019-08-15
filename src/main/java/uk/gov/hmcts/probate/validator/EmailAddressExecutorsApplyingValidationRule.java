package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class EmailAddressExecutorsApplyingValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String EMAIL_NOT_FOUND_PA = "multipleEmailsNotProvidedPA";

    public void validateEmails(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();

        if (caseData.getApplicationType().equals(ApplicationType.PERSONAL)) {
            caseData.getExecutorsApplyingNotifications().forEach(executor -> {
                if (executor.getValue().getNotification().equals(YES) && executor.getValue().getEmail().isEmpty()) {
                    String[] args = {caseDetails.getId().toString()};
                    String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_PA, args, Locale.UK);
                    throw new BusinessValidationException(userMessage, "An applying exec email is empty for case id " + caseDetails.getId());
                }
            });
        }
    }
}
