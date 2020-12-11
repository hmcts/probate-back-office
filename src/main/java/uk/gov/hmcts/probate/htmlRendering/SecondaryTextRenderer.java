package uk.gov.hmcts.probate.htmlRendering;

import java.util.regex.Pattern;

public class SecondaryTextRenderer {
    private SecondaryTextRenderer() {
        throw new IllegalStateException("Utility class");
    }

    // pre-condition - htmlTemplate contains <secText></secText>,
    public static String renderByReplace(String htmlTemplate) {
        return htmlTemplate == null ? null :
                htmlTemplate.replaceAll(Pattern.quote("<secText>"), "<font color=\"#505a5f\">")
                .replaceAll(Pattern.quote("</secText>"), "</font>");

    }
}
