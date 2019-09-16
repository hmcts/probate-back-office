package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
public class ExecutorsApplyingNotificationService {

    private List<CollectionMember<ExecutorsApplyingNotification>> executorList;

    public List<CollectionMember<ExecutorsApplyingNotification>> createExecutorList(CaseData caseData) {
        executorList = new ArrayList<>();
        if (caseData.getExecutorsApplyingNotifications() != null) {
            if (!caseData.getExecutorsApplyingNotifications().isEmpty()) {
                caseData.getExecutorsApplyingNotifications().clear();
            }
        }

        if (caseData.getApplicationType().equals(ApplicationType.PERSONAL)) {
            addPrimaryApplicant(caseData);
            addAdditionalExecutors(caseData);
        } else {
            addSolicitor(caseData);
        }


        return executorList;
    }

    private void addAdditionalExecutors(CaseData caseData) {
        if (caseData.getAdditionalExecutorsApplying() != null) {
            for (CollectionMember<AdditionalExecutorApplying> executorApplying : caseData.getAdditionalExecutorsApplying()) {
                executorList.add(buildExecutorList(executorApplying.getValue().getApplyingExecutorName(),
                        executorApplying.getValue().getApplyingExecutorEmail(),
                        executorApplying.getValue().getApplyingExecutorAddress()));

            }
        }
    }

    private void addPrimaryApplicant(CaseData caseData) {
        if (YES.equals(caseData.getPrimaryApplicantIsApplying())) {
            executorList.add(buildExecutorList(caseData.getPrimaryApplicantFullName(),
                    caseData.getPrimaryApplicantEmailAddress(), caseData.getPrimaryApplicantAddress()));
        }
    }

    private void addSolicitor(CaseData caseData) {
        executorList.add(buildExecutorList(caseData.getSolsSOTName(),
                caseData.getSolsSolicitorEmail(), caseData.getSolsSolicitorAddress()));

    }

    private CollectionMember<ExecutorsApplyingNotification> buildExecutorList(String name, String email, SolsAddress address) {
        return new CollectionMember<>(String.valueOf(executorList.size() + 1), ExecutorsApplyingNotification.builder()
                .name(name)
                .email(email)
                .address(address)
                .build());
    }
}
