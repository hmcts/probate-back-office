package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;


import static uk.gov.hmcts.probate.model.Constants.*;

@Component
public class AuthenticatedTranslationBusinessRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        return NO.equals(caseData.getEnglishWill());
    }
}