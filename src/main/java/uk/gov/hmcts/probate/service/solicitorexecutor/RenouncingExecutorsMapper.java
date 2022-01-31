package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@Service
public class RenouncingExecutorsMapper {
    private static final String RENOUNCED = "Renunciation";

    public List<AdditionalExecutorNotApplying> getAllRenouncingExecutors(CaseData caseData) {
        List<AdditionalExecutorNotApplying> executors = new ArrayList<>();
        System.out.println("**** getAllRenouncingExecutors");
        if (caseData.getSolsAdditionalExecutorList() != null) {
            executors.addAll(caseData.getSolsAdditionalExecutorList().stream()
                .map(CollectionMember::getValue)
                .filter(exec -> NO.equals(exec.getAdditionalApplying()))
                .filter(exec -> RENOUNCED.equals(exec.getAdditionalExecReasonNotApplying()))
                .map(exec -> AdditionalExecutorNotApplying.builder()
                    .notApplyingExecutorName(exec.getAdditionalExecForenames() + " " + exec.getAdditionalExecLastname())
                    .notApplyingExecutorReason(exec.getAdditionalExecReasonNotApplying())
                    .build())
                .collect(Collectors.toList()));
        }

        if (caseData.getAdditionalExecutorsNotApplying() != null) {
            executors.addAll(caseData.getAdditionalExecutorsNotApplying().stream()
                .map(CollectionMember::getValue)
                .filter(exec -> RENOUNCED.equals(exec.getNotApplyingExecutorReason()))
                .collect(Collectors.toList()));
        }

        if (caseData.getPrimaryApplicantForenames() != null && NO.equals(caseData.getPrimaryApplicantIsApplying()) 
            && RENOUNCED.equals(caseData.getSolsPrimaryExecutorNotApplyingReason())
        ) {
            executors.add(AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(caseData.getPrimaryApplicantForenames() + " " 
                    + caseData.getPrimaryApplicantSurname())
                .notApplyingExecutorReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
                .build());
        }

        return executors;
    }

}
