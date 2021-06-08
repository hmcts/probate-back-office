package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class SolsExecutorRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodySolsExecutor";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {

        if (GRANT_TYPE_ADMON.equals(caseData.getSolsWillType())
            && YES.equals(caseData.getSolsSolicitorIsExec())
            && NO.equals(caseData.getSolsSolicitorIsApplying())
            && caseData.getSolsSolicitorNotApplyingReason().matches("Renunciation")) {
            return false;

        } else if (GRANT_TYPE_ADMON.equals(caseData.getSolsWillType())
            && NO.equals(caseData.getSolsSolicitorIsExec())
            && NO.equals(caseData.getSolsSolicitorIsApplying())) {
            return false;
            
        } else if (GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType())
            && NO.equals(caseData.getSolsSolicitorIsExec())
            && NO.equals(caseData.getSolsSolicitorIsApplying())) {
            return false;

        } else {
            return true;
        }
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
