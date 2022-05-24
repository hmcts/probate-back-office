package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class NotarialWillBusinessRule implements BusinessRule {
    @Override
    public boolean isApplicable(CaseData caseData) {
        return YES.equals(caseData.getWillAccessNotarial());
    }
}
