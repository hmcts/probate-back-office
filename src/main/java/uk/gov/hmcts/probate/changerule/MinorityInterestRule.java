package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class MinorityInterestRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodyMinorityInterest";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return YES.equals(caseData.getSolsMinorityInterest());
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
