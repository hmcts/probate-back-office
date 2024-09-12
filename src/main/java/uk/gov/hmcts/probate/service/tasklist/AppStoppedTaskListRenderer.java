package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.htmlrendering.EmailAddressRenderer;
import uk.gov.hmcts.probate.htmlrendering.LinkRenderer;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.AppStoppedHtmlTemplate;

@Service
@RequiredArgsConstructor
public class AppStoppedTaskListRenderer extends NoTaskListRenderer {

    protected String renderContactDetails() {
        return "";
    }

    protected String renderBody(CaseDetails details) {

        return ParagraphRenderer.renderByReplace(
            EmailAddressRenderer.renderByReplace(AppStoppedHtmlTemplate.BASE_TEMPLATE,
                "probatefeedback@justice.gov.uk"))
            .replaceFirst("<paperformLink/>",
                LinkRenderer.renderOutside("paper form",
                    "https://www.gov.uk/government/collections/probate-forms"))
            .replaceFirst("<paperformLinkWelsh/>",
                    LinkRenderer.renderOutside("ffurflen bapur",
                            "https://www.gov.uk/government/collections/probate-forms"))
            .replaceFirst("<guidanceLink/>",
                LinkRenderer.renderOutside(
                    "Guidance on exemptions, conditions and when applications must be submitted by paper.",
                        "https://www.gov.uk/guidance/probate-paper-applications-for-legal-professionals"))
            .replaceFirst("<guidanceLinkWelsh/>",
                    LinkRenderer.renderOutside(
                            "Guidance on exemptions, conditions and when applications must be submitted by paper.",
                            "https://www.gov.uk/guidance/probate-paper-applications-for-legal-professionals"));
    }

    protected String getWhatNextText() {
        return "What to do next";
    }

    protected String getWhatNextTextWelsh() {
        return "Beth i'w wneud nesaf";
    }
}
