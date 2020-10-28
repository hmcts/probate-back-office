package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationState;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.tasklist.CaseEscalatedAlert;
import uk.gov.hmcts.probate.model.ccd.tasklist.CaseStoppedAlert;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskListUpdateService {

    public ResponseCaseDataBuilder generateTaskList(CaseDetails caseDetails, ResponseCaseDataBuilder builder) {

        BaseTaskListRenderer progressTabRenderer = TaskListRendererFactory.getTaskListRenderer(caseDetails.getState());
        String progressTabHtml = progressTabRenderer.renderHtml(caseDetails);
        builder.taskList(progressTabHtml);

        return builder;
    }
}
