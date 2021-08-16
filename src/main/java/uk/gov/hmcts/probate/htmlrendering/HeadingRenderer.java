package uk.gov.hmcts.probate.htmlrendering;

import java.util.regex.Pattern;

import static java.lang.String.format;

public class HeadingRenderer {
    private static final String OPEN_TAG = "<h2 class=\"govuk-heading-l\">";
    private static final String CLOSE_TAG = "</h2>";

    private HeadingRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String render(String headerText) {
        return format(OPEN_TAG + "%s" + CLOSE_TAG, headerText);
    }

    public static String renderByReplace(String html) {
        return html == null ? null :
                html.replaceAll(Pattern.quote("<h>"), OPEN_TAG)
                .replaceAll(Pattern.quote("</h>"), CLOSE_TAG);
    }
}
