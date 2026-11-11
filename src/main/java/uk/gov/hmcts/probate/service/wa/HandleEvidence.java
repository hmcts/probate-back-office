package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleEvidence implements CreateTaskProcessor {

    public static final String HANDLE_EVIDENCE
            = "handleEvidence";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return HANDLE_EVIDENCE;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData,
                        final String clientContext) {
        String caseType = callbackRequest.getCaseDetails().getData().getCaseType();
        log.info("Processing case id {} state {} with case type {}",
                callbackRequest.getCaseDetails().getId(),
                callbackRequest.getCaseDetails().getState(),
                caseType);
        switch (callbackRequest.getCaseDetails().getState()) {
            case STATE_CASE_PRINTED: {
                TaskTypes.getAwaitingDocIceTask(caseType).ifPresent(taskType -> {
                    updateTaskCreation(callbackRequest, responseCaseData, taskType);
                });

                break;
            }
            case STATE_BO_READY_TO_ISSUE: {
                TaskTypes.getReadyToIssueIceTask(caseType).ifPresent(taskType -> {
                    updateTaskCreation(callbackRequest, responseCaseData, taskType);
                });

                break;
            }
            case STATE_BO_CASE_WORKER_ESCALATION: {
                updateTaskCreation(callbackRequest, responseCaseData, TaskTypes.EXAMINE_SME_REFERRAL);

                break;
            }
            default:
                responseCaseData.setCreateTask(YES);
        }
    }

    private void updateTaskCreation(final CallbackRequest callbackRequest,
                                    final ResponseCaseData responseCaseData,
                                    final TaskTypes taskType) {
        waTaskService
                .setCreateTask(callbackRequest, responseCaseData, taskType);

    }
}
