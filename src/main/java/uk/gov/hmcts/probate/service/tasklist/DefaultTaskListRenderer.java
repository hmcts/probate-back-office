package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.*;
import uk.gov.hmcts.probate.model.caseProgress.TaskListState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseTaskListHtmlTemplate;

public class DefaultTaskListRenderer extends BaseTaskListRenderer {
    public String renderHtml(CaseDetails details) {
        TaskListState tlState = TaskListState.MapCaseState(details.getState());
        if (tlState == TaskListState.TL_STATE_NOT_APPLICABLE) {
            return "";
        }
        return
            TaskStateStatusRenderer.renderByReplace(tlState,
                    ParagraphRenderer.renderByReplace(
                        GridRenderer.renderByReplace(
                                SecondaryTextRenderer.renderByReplace(
                                        HeadingRenderer.renderByReplace(
                                                UnorderedListRenderer.renderByReplace(CaseTaskListHtmlTemplate.taskListTemplate))))));
    }
}
