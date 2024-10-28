package uk.gov.hmcts.probate.htmlrendering;

import static java.lang.String.format;

public class SubheadingRenderer {
    private SubheadingRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String render(String subheaderText, String subheaderTextWelsh) {
        return format("<h3 class=\"govuk-heading-m\">%s</h3>", subheaderText)
                + format("<h3 class=\"govuk-heading-m\">%s</h3>", subheaderTextWelsh);
    }
}
