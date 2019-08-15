package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
public class ExecutorsApplyingNotificationService {

    private List<ExecutorsApplyingNotification> executorList = new ArrayList<>();

    public List<ExecutorsApplyingNotification> createExecutorList(CaseData caseData) {
        caseData.getExecutorsApplyingNotifications().clear();
        addPrimaryApplicant(caseData);
        addAdditionalExecutors(caseData);

        return executorList;
    }

    private void addAdditionalExecutors(CaseData caseData) {
        for (CollectionMember<AdditionalExecutorApplying> executorApplying : caseData.getAdditionalExecutorsApplying()) {
            executorList.add(ExecutorsApplyingNotification.builder().name(executorApplying.getValue().getApplyingExecutorName()).email(executorApplying.getValue().getApplyingExecutorEmail()).build());
        }
    }

    private void addPrimaryApplicant(CaseData caseData) {
        if (caseData.getPrimaryApplicantIsApplying().equals(YES)) {
            executorList.add(ExecutorsApplyingNotification.builder().name(caseData.getPrimaryApplicantFullName()).email(caseData.getPrimaryApplicantEmailAddress()).build());
        }
    }
}
