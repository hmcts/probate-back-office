package uk.gov.hmcts.probate.htmlRendering;

import java.util.regex.Pattern;

import static java.lang.String.format;

public class HeadingRenderer {
    private static final String openTag = "<h2 class=\"govuk-heading-l\">";
    private static final String closeTag = "</h2>";

    public static String render(String headerText) {
        return format(openTag + "%s" + closeTag , headerText);
    }
    public static String renderByReplace(String html) {
        return html == null ? null :
                html.replaceAll(Pattern.quote("<h>"), openTag)
                .replaceAll(Pattern.quote("</h>"), closeTag);
    }
}
