package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.htmlrendering.GridRenderer;
import uk.gov.hmcts.probate.htmlrendering.HeadingRenderer;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlrendering.SecondaryTextRenderer;
import uk.gov.hmcts.probate.htmlrendering.UnorderedListRenderer;
import uk.gov.hmcts.probate.model.caseprogress.TaskListState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseTaskListHtmlTemplate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DefaultTaskListRenderer extends BaseTaskListRenderer {
    private final TaskStateRenderer taskStateRenderer;

    public String renderHtml(CaseDetails details) {
        final String paymentTaken = details.getData().getPaymentTaken();
        final String state = details.getState();
        final TaskListState tlState = TaskListState.mapCaseState(state, paymentTaken);
        if (tlState == TaskListState.TL_STATE_NOT_APPLICABLE) {
            return "";
        }
        final CaseData caseData = details.getData();
        final String submitDate = caseData.getApplicationSubmittedDate();
        final LocalDate submitLocalDate =
                submitDate == null || submitDate.equals("") ? null : LocalDate.parse(submitDate);
        final LocalDate authDate = caseData.getAuthenticatedDate();
        String willType = caseData.getSolsWillType();
        // switch statement inside rendering requires not null, default to gop
        // if not provided (though will not be relevant to returned html),
        // in order to prevent test failures
        if (willType == null) {
            willType = "WillLeft";
        }
        return
            taskStateRenderer.renderByReplace(tlState,
                ParagraphRenderer.renderByReplace(
                    GridRenderer.renderByReplace(
                        SecondaryTextRenderer.renderByReplace(
                            HeadingRenderer.renderByReplace(
                                UnorderedListRenderer.renderByReplace(CaseTaskListHtmlTemplate.TASK_LIST_TEMPLATE))))),
                                    details.getId(), willType, caseData.getSolsSOTNeedToUpdate(),
                                        authDate, submitLocalDate, details);
    }
}
