package uk.gov.hmcts.probate.validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAddressExecutorsValidationRule implements CaseDetailsEmailValidationRule {
    private static final String EMAIL_NOT_FOUND_PA = "multipleAddressNotProvidedPA";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Override
    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_PA, args, Locale.UK);
        CaseData caseData = caseDetails.getData();

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