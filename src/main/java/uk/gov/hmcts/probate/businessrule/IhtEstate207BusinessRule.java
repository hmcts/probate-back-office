package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;

@Component
public class IhtEstate207BusinessRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        boolean completed = YES.equals(caseData.getIhtFormEstateValuesCompleted());
        boolean form207 = IHT207_VALUE.equals(caseData.getIhtFormEstate());

        return completed && form207;
    }
}
