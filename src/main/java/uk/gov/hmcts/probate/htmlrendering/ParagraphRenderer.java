package uk.gov.hmcts.probate.htmlRendering;

import java.util.regex.Pattern;

public class ParagraphRenderer {
    private ParagraphRenderer() {
        throw new IllegalStateException("Utility class");
    }

    // pre-condition - paragraphedHtml contains <p></p> tags
    public static String renderByReplace(String paragraphedHtml) {
        return paragraphedHtml == null ? null :
                paragraphedHtml.replaceAll(Pattern.quote("<p>"), "<p class=\"govuk-body-s\">");
    }
}
