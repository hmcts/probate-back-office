package uk.gov.hmcts.probate.validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAddressExecutorsValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String EMAIL_NOT_FOUND_PA = "multipleEmailsNotProvidedPA";
    private static final String REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-z0-9](?:[a-z0-9-.]{0,30}[a-z0-9])?\\.[a-z0-9](?:[a-z0-9-]{0,10}[a-z0-9])?";

    @Override
    public void validate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_PA, args, Locale.UK);

        if(caseData.getAdditionalExecutorsApplying() != null) {
            caseData.getAdditionalExecutorsApplying().forEach(executor -> {
                if (executor.getValue().getApplyingExecutorEmail() != null)
                    if(!executor.getValue().getApplyingExecutorEmail().matches(REGEX)) {
                        throw new BusinessValidationException(userMessage,
                                "An applying exec email does not meet the criteria for case id " + caseDetails.getId());
                    }
            });
        }
    }
}
