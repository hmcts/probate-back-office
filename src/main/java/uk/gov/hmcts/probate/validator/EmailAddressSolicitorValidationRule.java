package uk.gov.hmcts.probate.validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import java.util.*;

@Component
@RequiredArgsConstructor
public class EmailAddressSolicitorValidationRule implements CaseDetailsEmailValidationRule {
    private static final String EMAIL_NOT_FOUND_PA = "solsSolicitorEmailInvalid";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Override
    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_PA, args, Locale.UK);

        if(caseDetails.getData().getApplicationType() != null){
            if (caseDetails.getData().getApplicationType().getCode().matches("sol")
                    && caseDetails.getData().getSolsSolicitorEmail() != null) {
                if(!caseDetails.getData().getSolsSolicitorEmail().matches(REGEX)){
                    throw new BusinessValidationException(userMessage,
                            "Solicitor's email does not meet the criteria for case id " + caseDetails.getId());
                }
            }
        }
    }
}
