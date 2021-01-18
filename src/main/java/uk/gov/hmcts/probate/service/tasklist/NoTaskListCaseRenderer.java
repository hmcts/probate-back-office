package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.HeadingRenderer;
import uk.gov.hmcts.probate.htmlrendering.LinkRenderer;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlrendering.SubheadingRenderer;
import uk.gov.hmcts.probate.model.htmltemplate.ContactDetailsHtmlTemplate;

import java.util.LinkedList;
import java.util.List;

// A render to render case progress html for when we don't want to display a task list
public abstract class NoTaskListCaseRenderer extends NoTaskListRenderer {

    public String renderContactDetails() {
        final List<String> lines = new LinkedList<>();

        lines.add(HeadingRenderer.render("Get help with your application"));
        lines.add(SubheadingRenderer.render("Telephone"));
        lines.add(ParagraphRenderer.renderByReplace(ContactDetailsHtmlTemplate.CONTACT_TEMPLATE)
                .replaceFirst("<englishPhoneNumber>", "0300 303 0648")
                .replaceFirst("<welshPhoneNumber>", "0300 303 0654")
        );
        lines.add(LinkRenderer.renderOutside("Find out about call charges", "https://www.gov.uk/call-charges"));
        lines.add(SubheadingRenderer.render("Email"));
        lines.add(ParagraphRenderer.renderByReplace(ContactDetailsHtmlTemplate.EMAIL_TEMPLATE)
                .replaceFirst("<email>", LinkRenderer.render("contactprobate@justice.gov.uk", "mailto:contactprobate@justice.gov.uk"))
        );

        return String.join("\n\n", lines);
    }

    protected String getWhatNextText() {
        return "What happens next";
    }
}
