package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class SolsExecutorRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodySolsExecutor";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return !GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType())
                && (YES.equals(caseData.getSolsSolicitorIsExec())
                || YES.equals(caseData.getSolsSolicitorIsApplying()));
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
