package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class ExecutorsRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodyNoApplyingExecutors";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        long numApplying = 0;
        if (caseData.getSolsAdditionalExecutorList() != null) {
            numApplying = caseData.getSolsAdditionalExecutorList().stream()
                    .map(CollectionMember::getValue)
                    .filter(additionalExecutor -> YES.equals(additionalExecutor.getAdditionalApplying()))
                    .count();
        }
        if (YES.equals(caseData.getPrimaryApplicantIsApplying())) {
            numApplying++;
        }

        return numApplying == 0;
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
