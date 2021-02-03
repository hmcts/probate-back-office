package uk.gov.hmcts.probate.htmlrendering;

import java.util.regex.Pattern;

public class EmailAddressRenderer {
    private EmailAddressRenderer() {
        throw new IllegalStateException("Utility class");
    }

    // pre-condition - html contains <[emailAddress]/>
    public static String renderByReplace(String html, String emailAddress) {
        return html == null ? null :
                html.replaceAll(Pattern.quote("<" + emailAddress + "/>"), "<a href=\"mailto:" + emailAddress
                        + "\" class=\"govuk-link\" target=\"_blank\">" + emailAddress + "</a>");
    }
}
