package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;


@Component
public class PA15FormBusinessRule implements BusinessRule {
    private static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";

    public boolean isApplicable(CaseData caseData) {
        boolean solIsExec = YES.equals(caseData.getSolsSolicitorIsExec());
        boolean solNotApplying = NO.equals(caseData.getSolsSolicitorIsApplying());
        boolean solRenounced =
            REASON_FOR_NOT_APPLYING_RENUNCIATION.equals(caseData.getSolsSolicitorNotApplyingReason());
        boolean solExecNotApplyingRenounced = solIsExec && solNotApplying && solRenounced;
        if (solExecNotApplyingRenounced) {
            return true;
        }

        boolean primaryNotApplying = NO.equals(caseData.getPrimaryApplicantIsApplying());
        boolean primaryRenounced =
            REASON_FOR_NOT_APPLYING_RENUNCIATION.equals(caseData.getSolsPrimaryExecutorNotApplyingReason());
        boolean primaryNotApplyingRenouced = primaryNotApplying && primaryRenounced;
        if (primaryNotApplyingRenouced) {
            return true;
        }
        
        boolean otherExecs = YES.equals(caseData.getOtherExecutorExists());
        if (otherExecs) {
            for (CollectionMember<AdditionalExecutor> cm : caseData.getSolsAdditionalExecutorList()) {
                boolean notApplying = NO.equals(cm.getValue().getAdditionalApplying());
                if (notApplying) {
                    boolean renounced = 
                        REASON_FOR_NOT_APPLYING_RENUNCIATION.equals(cm.getValue().getAdditionalExecReasonNotApplying());
                    if (renounced) {
                        return true;
                    }
                }
            }
        }

        
        return false;
    }
}
