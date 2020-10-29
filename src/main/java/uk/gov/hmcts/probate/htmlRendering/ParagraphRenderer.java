package uk.gov.hmcts.probate.htmlRendering;

public class ParagraphRenderer {
    // pre-condition - paragraphedHtml contains <p></p> tags
    public static String render(String paragraphedHtml) {
        return paragraphedHtml.replaceAll("<p>", "<p class='govuk-body-s'>");
    }
}
