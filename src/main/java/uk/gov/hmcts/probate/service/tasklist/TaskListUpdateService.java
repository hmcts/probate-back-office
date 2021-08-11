package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskListUpdateService {
    TaskListRendererFactory factory = new TaskListRendererFactory();

    public ResponseCaseDataBuilder generateTaskList(CaseDetails caseDetails, ResponseCaseDataBuilder builder) {

        BaseTaskListRenderer progressTabRenderer = factory.getTaskListRenderer(caseDetails.getState());
        String progressTabHtml = progressTabRenderer.renderHtml(caseDetails);
        builder.taskList(progressTabHtml);

        return builder;
    }
}
