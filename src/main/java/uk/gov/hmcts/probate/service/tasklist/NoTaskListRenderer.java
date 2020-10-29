package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.HeadingRenderer;
import uk.gov.hmcts.probate.htmlRendering.LinkRenderer;
import uk.gov.hmcts.probate.htmlRendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlRendering.SubheadingRenderer;
import uk.gov.hmcts.probate.model.CaseProgressState;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.ContactDetailsHtmlTemplate;

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
        lines.add(renderContactDetails());

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
        return HeadingRenderer.render("What happens next");
    }

    public String renderContactDetails() {
        final List<String> lines = new LinkedList<>();

        lines.add(HeadingRenderer.render("Get help with your application"));
        lines.add(SubheadingRenderer.render("Telephone"));
        lines.add(ParagraphRenderer.renderByReplace(ContactDetailsHtmlTemplate.contactTemplate)
                .replaceFirst("<englishPhoneNumber>", "0300 303 0648")
                .replaceFirst("<welshPhoneNumber>", "0300 303 0654")
        );
        lines.add(LinkRenderer.render("Find out about call charges", "https://www.gov.uk/call-charges"));
        lines.add(SubheadingRenderer.render("Email"));
        lines.add(ParagraphRenderer.renderByReplace(ContactDetailsHtmlTemplate.emailTemplate)
                .replaceFirst("<email>", LinkRenderer.render("contactprobate@justice.gov.uk", "mailto:contactprobate@justice.gov.uk"))
        );

        return String.join("\n\n", lines);
    }

    abstract protected String renderBody(CaseDetails caseDetails);
}
