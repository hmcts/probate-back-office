package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class AmendCaseDetailsForReadyToIssue extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    private static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";
    private static final String BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE = "boAmendCaseDetailsForReadyToIssue";
    private static final Set<TaskTypes> TASKS_AWAITING_DOCUMENTATION_TO_CLOSE = TaskTypes.examineAwaitingDocIceTasks();
    private static final Set<TaskTypes> TASKS_READY_TO_ISSUE_TO_CREATE = TaskTypes.examineReadyToIssueIceTasks();

    private final WaTaskService waTaskService;

    public AmendCaseDetailsForReadyToIssue(WaTaskService waTaskService) {
        super(waTaskService);
        this.waTaskService = waTaskService;
    }

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        boolean taskToClosePresent = !caseTypeChanged
                && waTaskService.isTaskPresent(authToken,
                callbackRequest.getCaseDetails().getId().toString(),
                BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                TASKS_AWAITING_DOCUMENTATION_TO_CLOSE);

        Result result = getResult(
                callbackRequest,
                responseCaseData,
                request -> {
                    Long caseId = request.getCaseDetails().getId();
                    Set<String> allAssociatedTasks = waTaskService.getAllAssociatedTasks(authToken,
                            caseId.toString(),
                            BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION);
                    log.info("case id {}: allAssociatedTasks {}", caseId, allAssociatedTasks);
                    return allAssociatedTasks.isEmpty();
                });

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                && !result.caseHandedOffToLegacySite()
                && !waTaskService.isTaskPresent(authToken,
                        callbackRequest.getCaseDetails().getId().toString(),
                        BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE,
                        TASKS_READY_TO_ISSUE_TO_CREATE);

        responseCaseData.setCreateTask(caseTypeChanged || taskToClosePresent
                || result.newHandOffPresent() || isInitialCaseExaminationTaskRequired
                ? YES : Constants.NO);

        log.info("case id {}: caseTypeChanged {}, taskToClosePresent {}, newHandOffPresent {}"
                        + " new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                taskToClosePresent,
                result.newHandOffPresent(),
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }
}
