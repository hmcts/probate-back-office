package uk.gov.hmcts.probate.htmlRendering;

public class GridRenderer {
    // pre-condition - htmlTemplate contains <gridRow></gridRow>, <gridCol-two-thirds></gridCol-two-thirds>,
    // <gridCol-one-third></gridCol-one-third> & <gridRowSeparator/> tags
    // and potentially <gridRow></gridRow> & <gridRowSeparator/> tags
    public static String renderByReplace(String htmlTemplate) {
        return htmlTemplate == null ? null :
                htmlTemplate.replaceAll("<gridRow>", "<div class=\"govuk-grid-row\">")
                .replaceAll("</gridRow>", "</div>")
                .replaceAll("<gridCol-two-thirds>", "<div class=\"govuk-grid-column-two-thirds\">")
                .replaceAll("</gridCol-two-thirds>", "</div>")
                .replaceAll("<gridCol-one-third>", "<div class=\"govuk-grid-column-one-third\">")
                .replaceAll("</gridCol-one-third>", "</div>")
                .replaceAll("<gridRowSeparator/>", "<hr class=\"govuk-section-break govuk-section-break--m govuk-section-break--visible\">\n");
    }
}
