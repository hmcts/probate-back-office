package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_TRUST_CORP;


@Component
public class TCResolutionLodgedWithApplicationRule implements BusinessRule {

    @Override
    public boolean isApplicable(CaseData caseData) {
        return TITLE_AND_CLEARING_TRUST_CORP.equalsIgnoreCase(caseData.getTitleAndClearingType());
    }
}
