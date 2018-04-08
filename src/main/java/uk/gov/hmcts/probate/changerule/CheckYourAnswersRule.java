package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Component
public class CheckYourAnswersRule implements ChangeRule {
    private static final String MESSAGE_KEY = "willStopBodyNoWill";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return (YES.equals(caseData.getSolsCYANeedToUpdate()));
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }

}
