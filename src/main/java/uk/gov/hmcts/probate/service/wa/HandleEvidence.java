package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import static uk.gov.hmcts.probate.model.Constants.YES;
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
        responseCaseData.setCreateTask(YES);
        String caseType = callbackRequest.getCaseDetails().getData().getCaseType();
        switch (callbackRequest.getCaseDetails().getState()) {
            case STATE_CASE_PRINTED: {
                waTaskService
                        .setCreateTask(callbackRequest, responseCaseData,  TaskTypes.getAwaitingDocIceTask(caseType));
                break;
            }
            case STATE_BO_READY_TO_ISSUE: {
                waTaskService
                        .setCreateTask(callbackRequest, responseCaseData, TaskTypes.getReadyToIssueIceTask(caseType));
                break;
            }
        }
    }
}
