package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.HeaderRenderer;
import uk.gov.hmcts.probate.model.CaseProgressState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

// A render to render case progress html for when we don't want to display a task list
public abstract class NoTaskListRenderer extends BaseTaskListRenderer {

    public String renderHtml(CaseDetails caseDetails) {

        final List<String> lines = new LinkedList<>();

        lines.add("<div class=\"width-50\">");

        lines.add(renderMainHeader());
        lines.add(renderInset(caseDetails));
        lines.add(renderBodyHeader());
        lines.add(renderBody(caseDetails));

        lines.add("</div>");

        return String.join("\n\n", lines);
    }

    private String renderInset(CaseDetails caseDetails) {
        String caseState = caseDetails.getState();
        String progressStateName = "Unknown";
        if (caseState != null) {
            CaseProgressState progressState = CaseProgressState.MapCaseState(caseState);
            progressStateName = progressState.getDisplayText();
        }

        return format("<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">%s</div>", progressStateName);
    }

    private String renderBodyHeader() {
        return new HeaderRenderer().render("What happens next");
    }

    abstract protected String renderBody(CaseDetails caseDetails);
}
