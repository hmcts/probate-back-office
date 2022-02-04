package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE;
import static uk.gov.hmcts.probate.model.Constants.YES;


@Component
public class PA14FormBusinessRule extends NotApplyingExecutorsBusinessRule {

    public boolean isApplicable(CaseData caseData) {
        if (isSolExecNotApplying(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE)) {
            return true;
        }

        if (isPrimaryNotApplying(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE)) {
            return true;
        }

        boolean otherExecs = YES.equals(caseData.getOtherExecutorExists());
        if (otherExecs) {
            if (anySolsAdditionalExecs(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE)) {
                return true;
            }

            if (anyAdditionalExecsNotApplying(caseData, REASON_FOR_NOT_APPLYING_MENTALLY_INCAPABLE)) {
                return true;
            }
        }

        return false;
    }
}
