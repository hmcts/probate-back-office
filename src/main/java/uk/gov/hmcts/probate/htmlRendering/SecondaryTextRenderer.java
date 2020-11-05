package uk.gov.hmcts.probate.htmlRendering;

public class SecondaryTextRenderer {
    // pre-condition - paragraphedHtml contains <grid></grid>,
    // and potentially <gridRow></gridRow> & <gridRowSeparator> tags
    public static String renderByReplace(String htmlTemplate) {
        return htmlTemplate == null ? null :
                htmlTemplate.replaceAll("<secText>", "<font color=\"#505a5f\">")
                .replaceAll("</secText>", "</font>");

    }
}
