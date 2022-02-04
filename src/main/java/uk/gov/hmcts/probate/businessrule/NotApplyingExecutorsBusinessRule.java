package uk.gov.hmcts.probate.businessrule;

import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public abstract class NotApplyingExecutorsBusinessRule implements BusinessRule {

    boolean isPrimaryNotApplying(CaseData caseData, String notApplyingReason) {
        boolean primaryNotApplying = NO.equals(caseData.getPrimaryApplicantIsApplying());
        boolean hasReason = notApplyingReason.equals(caseData.getSolsPrimaryExecutorNotApplyingReason());
        return primaryNotApplying && hasReason;
    }

    boolean isSolExecNotApplying(CaseData caseData, String notApplyingReason) {
        boolean solIsExec = YES.equals(caseData.getSolsSolicitorIsExec());
        boolean solNotApplying = NO.equals(caseData.getSolsSolicitorIsApplying());
        boolean hasReason = notApplyingReason.equals(caseData.getSolsSolicitorNotApplyingReason());
        return solIsExec && solNotApplying && hasReason;
    }

    boolean anyAdditionalExecsNotApplying(CaseData caseData, String notApplyingReason) {
        if (caseData.getAdditionalExecutorsNotApplying() != null) {
            for (CollectionMember<AdditionalExecutorNotApplying> cm :
                caseData.getAdditionalExecutorsNotApplying()) {
                boolean hasReason = notApplyingReason.equals(cm.getValue().getNotApplyingExecutorReason());
                if (hasReason) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean anySolsAdditionalExecs(CaseData caseData, String notApplyingReason) {
        if (caseData.getSolsAdditionalExecutorList() != null) {
            for (CollectionMember<AdditionalExecutor> cm : caseData.getSolsAdditionalExecutorList()) {
                boolean notApplying = NO.equals(cm.getValue().getAdditionalApplying());
                if (notApplying) {
                    boolean hasReason = notApplyingReason.equals(cm.getValue().getAdditionalExecReasonNotApplying());
                    if (hasReason) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
