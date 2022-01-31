package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;


@Component
public class PA15FormBusinessRule implements BusinessRule {
    private static final String RENOUNCED = "Renunciation";

    public boolean isApplicable(CaseData caseData) {
        if (isSolExecNotApplyingRenounced(caseData)) {
            return true;
        }

        if (isPrimaryNotApplyingRenouced(caseData)) {
            return true;
        }

        boolean otherExecs = YES.equals(caseData.getOtherExecutorExists());
        if (otherExecs) {
            if (anySolsAdditionalExecsRenounced(caseData)) {
                return true;
            }

            if (anyAdditionalExecsNotApplyingRenouonced(caseData)) {
                return true;
            }
        }

        return false;
    }

    private boolean anyAdditionalExecsNotApplyingRenouonced(CaseData caseData) {
        if (caseData.getAdditionalExecutorsNotApplying() != null) {
            for (CollectionMember<AdditionalExecutorNotApplying> cm :
                caseData.getAdditionalExecutorsNotApplying()) {
                boolean renounced = RENOUNCED.equals(cm.getValue().getNotApplyingExecutorReason());
                if (renounced) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean anySolsAdditionalExecsRenounced(CaseData caseData) {
        if (caseData.getSolsAdditionalExecutorList() != null) {
            System.out.println("**** anySolsAdditionalExecsRenouonced");
            for (CollectionMember<AdditionalExecutor> cm : caseData.getSolsAdditionalExecutorList()) {
                boolean notApplying = NO.equals(cm.getValue().getAdditionalApplying());
                if (notApplying) {
                    boolean renounced = RENOUNCED.equals(cm.getValue().getAdditionalExecReasonNotApplying());
                    if (renounced) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private boolean isPrimaryNotApplyingRenouced(CaseData caseData) {
        boolean primaryNotApplying = NO.equals(caseData.getPrimaryApplicantIsApplying());
        boolean primaryRenounced = RENOUNCED.equals(caseData.getSolsPrimaryExecutorNotApplyingReason());
        return primaryNotApplying && primaryRenounced;
    }

    private boolean isSolExecNotApplyingRenounced(CaseData caseData) {
        boolean solIsExec = YES.equals(caseData.getSolsSolicitorIsExec());
        boolean solNotApplying = NO.equals(caseData.getSolsSolicitorIsApplying());
        boolean solRenounced = RENOUNCED.equals(caseData.getSolsSolicitorNotApplyingReason());
        return solIsExec && solNotApplying && solRenounced;
    }
}
