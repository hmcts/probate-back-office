package uk.gov.hmcts.probate.model.htmlTemplate;

public class StatusTagHtmlTemplate {
    public static final String statusTag = "<p><img align=\"right\" width=\"92px\" height=\"25px\" src=\"<imgSrc/>\" alt=\"<imgAlt/>\" title=\"<imgTitle/>\" /></p>\n";

    private StatusTagHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
