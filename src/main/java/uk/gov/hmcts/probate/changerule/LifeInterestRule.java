package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class LifeInterestRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodyLifeInterest";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return YES.equals(caseData.getSolsLifeInterest());
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
