package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.HeadingRenderer;
import uk.gov.hmcts.probate.htmlrendering.LinkRenderer;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlrendering.SubheadingRenderer;
import uk.gov.hmcts.probate.model.caseprogress.CaseProgressState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.ContactDetailsHtmlTemplate;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public abstract class NoTaskListRenderer extends BaseTaskListRenderer {
    public String renderHtml(CaseDetails caseDetails) {

        final List<String> lines = new LinkedList<>();

        lines.add("<div class=\"width-50\">");

        lines.add(renderMainHeader());
        lines.add(renderInset(caseDetails));
        lines.add(renderBodyHeader());
        lines.add(renderBody(caseDetails));
        lines.add(renderContactDetails());

        lines.add("</div>");

        return String.join("\n\n", lines);
    }

    private String renderInset(CaseDetails caseDetails) {
        String caseState = caseDetails.getState();
        String progressStateName = "Unknown";
        if (caseState != null) {
            CaseProgressState progressState = CaseProgressState.mapCaseState(caseState);
            progressStateName = progressState.getDisplayText();
        }

        return format("<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">%s</div>", progressStateName);
    }

    private String renderBodyHeader() {
        return HeadingRenderer.render(getWhatNextText());
    }

    protected abstract String renderContactDetails();
    protected abstract String renderBody(CaseDetails caseDetails);
    protected abstract String getWhatNextText();

}
