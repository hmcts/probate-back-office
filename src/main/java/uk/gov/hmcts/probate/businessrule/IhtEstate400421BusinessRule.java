package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

@Component
public class IhtEstate400421BusinessRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        boolean completed = YES.equals(caseData.getIhtFormEstateValuesCompleted());
        boolean form400421 = IHT400421_VALUE.equals(caseData.getIhtFormEstate());

        return completed && form400421;
    }
}
