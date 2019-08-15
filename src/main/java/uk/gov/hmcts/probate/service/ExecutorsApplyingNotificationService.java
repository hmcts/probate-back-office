package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        addPrimaryApplicant(caseData);
        addAdditionalExecutors(caseData);

        return executorList;
    }

    private void addAdditionalExecutors(CaseData caseData) {
        for (CollectionMember<AdditionalExecutorApplying> executorApplying : caseData.getAdditionalExecutorsApplying()) {
            executorList.add(buildExecutorList(executorApplying.getValue().getApplyingExecutorName(), executorApplying.getValue().getApplyingExecutorEmail(), executorApplying.getValue().getApplyingExecutorAddress()));

        }
    }

    private void addPrimaryApplicant(CaseData caseData) {
        if (caseData.getPrimaryApplicantIsApplying().equals(YES)) {
            executorList.add(buildExecutorList(caseData.getPrimaryApplicantFullName(), caseData.getPrimaryApplicantEmailAddress(), caseData.getPrimaryApplicantAddress()));
        }
    }

    private CollectionMember<ExecutorsApplyingNotification> buildExecutorList(String name, String email, SolsAddress address) {
        return new CollectionMember<>(null, ExecutorsApplyingNotification.builder()
                .name(name)
                .email(email)
                .address(address)
                .build());
    }
}
