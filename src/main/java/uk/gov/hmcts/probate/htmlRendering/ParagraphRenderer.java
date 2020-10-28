package uk.gov.hmcts.probate.htmlRendering;

import static java.lang.String.format;

public class ParagraphRenderer {
    // pre-condition - paragraphedHtml contains <p></p> tags
    public String render(String paragraphedHtml) {
        return paragraphedHtml.replaceAll("<p>", "<p class=\"govuk-body-s\">");
    }
}
