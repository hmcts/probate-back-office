package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskData;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AmendCaseDetailsForReadyToIssue implements CreateTaskProcessor {
    private final WaTaskService waTaskService;
    private final List<String> taskToCLose = List.of("ExamineDigitalCaseProbate",
            "ExamineDigitalCaseIntestacy",
            "ExamineDigitalCaseAdmonWill",
            "ExamineDigitalCaseAdColligendaBona");

    @Override
    public String getEventId() {
        return "boAmendCaseDetailsForReadyToIssue";
    }

    @Override
    public void process(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = !callbackRequest.getCaseDetails().getData().getCaseType()
                .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType());


        boolean taskToClosePresent = waTaskService.searchTasks(callbackRequest.getCaseDetails().getId().toString())
                .stream()
                .map(TaskData::getName)
                .anyMatch(taskToCLose::contains);

        responseCaseData.setCreateTask(caseTypeChanged || taskToClosePresent
                ? Constants.YES : Constants.NO);
    }
}
