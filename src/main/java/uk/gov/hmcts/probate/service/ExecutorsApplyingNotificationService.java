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

    public List<CollectionMember<ExecutorsApplyingNotification>> createExecutorList(CaseData caseData) {
        List<CollectionMember<ExecutorsApplyingNotification>> executorList = new ArrayList<>();
        if (caseData.getExecutorsApplyingNotifications() != null) {
            if (!caseData.getExecutorsApplyingNotifications().isEmpty()) {
                caseData.getExecutorsApplyingNotifications().clear();
            }
        }

        if (caseData.getApplicationType().equals(ApplicationType.PERSONAL)) {
            addPrimaryApplicant(caseData, executorList);
            addAdditionalExecutors(caseData, executorList);
        } else {
            addSolicitor(caseData, executorList);
        }


        return executorList;
    }

    private void addAdditionalExecutors(
            final CaseData caseData,
            final List<CollectionMember<ExecutorsApplyingNotification>> executorList) {
        if (caseData.getAdditionalExecutorsApplying() != null) {
            for (CollectionMember<AdditionalExecutorApplying> executorApplying : caseData
                .getAdditionalExecutorsApplying()) {
                executorList.add(buildExecutorList(
                        executorApplying.getValue().getApplyingExecutorName(),
                        executorApplying.getValue().getApplyingExecutorEmail(),
                        executorApplying.getValue().getApplyingExecutorAddress(),
                        executorList.size()));

            }
        }
    }

    private void addPrimaryApplicant(
            final CaseData caseData,
            final List<CollectionMember<ExecutorsApplyingNotification>> executorList) {
        if (YES.equals(caseData.getPrimaryApplicantIsApplying()) || caseData.getPrimaryApplicantIsApplying() == null) {
            executorList.add(buildExecutorList(
                    caseData.getPrimaryApplicantFullName(),
                    caseData.getPrimaryApplicantEmailAddress(),
                    caseData.getPrimaryApplicantAddress(),
                    executorList.size()));
        }
    }

    private void addSolicitor(
            final CaseData caseData,
            final List<CollectionMember<ExecutorsApplyingNotification>> executorList) {
        executorList.add(buildExecutorList(
                caseData.getSolsSOTName(),
                caseData.getSolsSolicitorEmail(),
                caseData.getSolsSolicitorAddress(),
                executorList.size()));

    }

    private CollectionMember<ExecutorsApplyingNotification> buildExecutorList(
            final String name,
            final String email,
            final SolsAddress address,
            final int execListSize) {
        return new CollectionMember<>(String.valueOf(execListSize + 1), ExecutorsApplyingNotification.builder()
            .name(name)
            .email(email)
            .address(address)
            .build());
    }
}
