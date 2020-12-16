package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.*;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseTaskListHtmlTemplate;

import java.time.LocalDate;

public class DefaultTaskListRenderer extends BaseTaskListRenderer {

    public String renderHtml(CaseDetails details) {
        final TaskListState tlState = TaskListState.mapCaseState(details.getState());
        if (tlState == TaskListState.TL_STATE_NOT_APPLICABLE) {
            return "";
        }
        final CaseData caseData = details.getData();
        final String submitDate = caseData.getApplicationSubmittedDate();
        final LocalDate submitLocalDate = submitDate == null || submitDate.equals("") ? null : LocalDate.parse(submitDate);
        final LocalDate authDate = caseData.getAuthenticatedDate();
        String willType = caseData.getSolsWillType();
        // switch statement inside rendering requires not null, default to gop if not provided (though will not be relevant to returned html),
        // in order to prevent test failures
        if (willType == null) {
            willType = "WillLeft";
        }
        return
            TaskStateRenderer.renderByReplace(tlState,
                    ParagraphRenderer.renderByReplace(
                        GridRenderer.renderByReplace(
                                SecondaryTextRenderer.renderByReplace(
                                        HeadingRenderer.renderByReplace(
                                                UnorderedListRenderer.renderByReplace(CaseTaskListHtmlTemplate.TASK_LIST_TEMPLATE))))),
                                                    details.getId(), willType, authDate, submitLocalDate);
    }
}
