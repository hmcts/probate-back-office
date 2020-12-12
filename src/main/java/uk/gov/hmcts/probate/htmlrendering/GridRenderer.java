package uk.gov.hmcts.probate.htmlRendering;

import java.util.regex.Pattern;

public class GridRenderer {
    private GridRenderer() {
        throw new IllegalStateException("Utility class");
    }

    private static final String DIV_CLOSE = "</div>";
    // pre-condition - htmlTemplate contains <gridRow></gridRow>, <gridCol-two-thirds></gridCol-two-thirds>,
    // <gridCol-one-third></gridCol-one-third> & <gridRowSeparator/> tags
    // and potentially <gridRow></gridRow> & <gridRowSeparator/> tags
    public static String renderByReplace(String htmlTemplate) {
        return htmlTemplate == null ? null :
                htmlTemplate.replaceAll( Pattern.quote("<gridRow>"), "<div class=\"govuk-grid-row\">")
                .replaceAll(Pattern.quote("</gridRow>"), DIV_CLOSE)
                .replaceAll(Pattern.quote("<gridCol-two-thirds>"), "<div class=\"govuk-grid-column-two-thirds\">")
                .replaceAll(Pattern.quote("</gridCol-two-thirds>"), DIV_CLOSE)
                .replaceAll(Pattern.quote("<gridCol-one-third>"), "<div class=\"govuk-grid-column-one-third\">")
                .replaceAll(Pattern.quote("</gridCol-one-third>"), DIV_CLOSE)
                .replaceAll(Pattern.quote("<gridRowSeparator/>"), "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n");
    }
}
