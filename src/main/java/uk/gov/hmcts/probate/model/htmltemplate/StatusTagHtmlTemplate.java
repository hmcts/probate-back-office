package uk.gov.hmcts.probate.model.htmltemplate;

public class StatusTagHtmlTemplate {
    public static final String STATUS_TAG = "<p><img align=\"right\" width=\"114px\" height=\"31px\" src=\"<imgSrc/>\" alt=\"<imgAlt/>\" title=\"<imgTitle/>\" /></p>\n";

    private StatusTagHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
