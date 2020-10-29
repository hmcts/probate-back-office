package uk.gov.hmcts.probate.htmlRendering;

import static java.lang.String.format;

public class HeadingRenderer {
    public static String render(String headerText) {
        return format("<h2 class=\"govuk-heading-l\">%s</h2>", headerText);
    }
}
