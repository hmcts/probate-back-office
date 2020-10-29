package uk.gov.hmcts.probate.htmlRendering;

public class ParagraphRenderer {
    // pre-condition - paragraphedHtml contains <p></p> tags
    public static String renderByReplace(String paragraphedHtml) {
        return paragraphedHtml == null ? null :
                paragraphedHtml.replaceAll("<p>", "<p class=\"govuk-body-s\">");
    }
}
