package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.WILL_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class SolsExecutorRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodySolsExecutor";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        if (!WILL_TYPE_PROBATE.equals(caseData.getSolsWillType())) {
            if (YES.equals(caseData.getSolsSolicitorIsExec()) && NO.equals(caseData.getSolsSolicitorIsMainApplicant())) {
                return true;
            }

            if (YES.equals(caseData.getSolsSolicitorIsMainApplicant()) && NO.equals(caseData.getSolsSolicitorIsApplying())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
