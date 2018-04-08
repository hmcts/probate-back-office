package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExecutorsRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodyNoApplyingExecutors";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        int numApplying = 0;
        if (caseData.getSolsAdditionalExecutorList() != null) {
            List<AdditionalExecutors> applyingExecutorsList = caseData.getSolsAdditionalExecutorList().stream()
                .filter(additionalExecutors -> YES.equals(additionalExecutors.getAdditionalExecutor().getAdditionalApplying()))
                .collect(Collectors.toList());
            numApplying = applyingExecutorsList.size();
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
