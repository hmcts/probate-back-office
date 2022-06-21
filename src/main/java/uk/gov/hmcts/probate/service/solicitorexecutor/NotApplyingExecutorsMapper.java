package uk.gov.hmcts.probate.service.solicitorexecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static uk.gov.hmcts.probate.model.Constants.NO;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Slf4j
@Service
public class NotApplyingExecutorsMapper {

    public List<AdditionalExecutorNotApplying> getAllExecutorsNotApplying(CaseData caseData, String notApplyingReason) {
        List<AdditionalExecutorNotApplying> executors = new ArrayList<>();
        if (caseData.getSolsAdditionalExecutorList() != null) {
            executors.addAll(caseData.getSolsAdditionalExecutorList().stream()
                .map(CollectionMember::getValue)
                .filter(exec -> NO.equals(exec.getAdditionalApplying()))
                .filter(exec -> notApplyingReason.equals(exec.getAdditionalExecReasonNotApplying()))
                .map(exec -> AdditionalExecutorNotApplying.builder()
                    .notApplyingExecutorName(exec.getAdditionalExecForenames() + " " + exec.getAdditionalExecLastname())
                    .notApplyingExecutorReason(exec.getAdditionalExecReasonNotApplying())
                    .build())
                .collect(Collectors.toList()));
        }

        if (caseData.getAdditionalExecutorsNotApplying() != null) {
            executors.addAll(caseData.getAdditionalExecutorsNotApplying().stream()
                .map(CollectionMember::getValue)
                .filter(exec -> notApplyingReason.equals(exec.getNotApplyingExecutorReason()))
                .collect(Collectors.toList()));
        }

        boolean sotExecNotIncludedAready =
            executors.stream().filter(exec -> exec.hasName(caseData.getSolsSOTName()))
                .collect(Collectors.toList()).isEmpty();
        if (sotExecNotIncludedAready && caseData.getSolsSOTName() != null
            && NO.equals(caseData.getSolsSolicitorIsApplying())
            && notApplyingReason.equals(caseData.getSolsSolicitorNotApplyingReason())
        ) {
            executors.add(AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(caseData.getSolsSOTName())
                .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
                .build());
        }

        return executors;
    }

}
