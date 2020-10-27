package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.HeaderRenderer;
import uk.gov.hmcts.probate.model.CaseProgressState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static java.lang.String.format;

// A render to render case progress html for when we don't want to display a task list
public abstract class NoTaskListRenderer extends BaseTaskListRenderer {
    public String renderHtml(CaseDetails caseDetails) {
        StringBuilder sb = new StringBuilder("<div class='width-50'>");
        sb.append(new HeaderRenderer().render("Case progress"));
        String inset = renderInset(caseDetails);
        sb.append(inset);
        sb.append("\n");

        String header = renderBodyHeader();
        sb.append("\n");

        sb.append(header);
        String body = renderBody(caseDetails);
        sb.append("\n");

        sb.append(body);
        sb.append("</div>"); // close the wrapper div
        return sb.toString();
    }

    private String renderInset(CaseDetails caseDetails) {
        String caseState = caseDetails.getState();
        String progressStateName = "Unknown";
        if (caseState != null) {
            CaseProgressState progressState = CaseProgressState.MapCaseState(caseState);
            progressStateName = progressState.name();
        }
        return format("<div class=\"govuk-inset-text\">%s</div>", progressStateName);
    }

    private String renderBodyHeader() {
        return new HeaderRenderer().render("What happens next");
    }

    abstract protected String renderBody(CaseDetails caseDetails);
}
