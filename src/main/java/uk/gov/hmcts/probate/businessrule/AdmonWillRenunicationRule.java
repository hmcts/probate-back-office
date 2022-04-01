package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Component
public class AdmonWillRenunicationRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        return Constants.GRANT_TYPE_ADMON.equals(caseData.getSolsWillType());
    }
}

