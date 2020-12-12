package uk.gov.hmcts.probate.htmlRendering;

import static java.lang.String.format;

public class SubheadingRenderer {
    private SubheadingRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String render(String subheaderText) {
        return format("<h3 class=\"govuk-heading-m\">%s</h3>", subheaderText);
    }
}
