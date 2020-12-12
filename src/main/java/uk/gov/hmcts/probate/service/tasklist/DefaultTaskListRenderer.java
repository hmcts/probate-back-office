package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.*;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseTaskListHtmlTemplate;

import java.time.LocalDate;

public class DefaultTaskListRenderer extends BaseTaskListRenderer {

    public String renderHtml(CaseDetails details) {
        TaskListState tlState = TaskListState.mapCaseState(details.getState());
        if (tlState == TaskListState.TL_STATE_NOT_APPLICABLE) {
            return "";
        }
        String submitDate = details.getData().getApplicationSubmittedDate();
        LocalDate submitLocalDate = submitDate == null || submitDate.equals("") ? null : LocalDate.parse(submitDate);
        LocalDate authDate = details.getData().getAuthenticatedDate();

        return
            TaskStateRenderer.renderByReplace(tlState,
                    ParagraphRenderer.renderByReplace(
                        GridRenderer.renderByReplace(
                                SecondaryTextRenderer.renderByReplace(
                                        HeadingRenderer.renderByReplace(
                                                UnorderedListRenderer.renderByReplace(CaseTaskListHtmlTemplate.TASK_LIST_TEMPLATE))))),
                                                    details.getId(), authDate, submitLocalDate);
    }
}
