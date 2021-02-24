package uk.gov.hmcts.probate.htmlrendering;

import static java.lang.String.format;

public class LinkRenderer {
    private LinkRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String render(String linkText, String link) {
        return format("<a href=\"%s\" class=\"govuk-link\">%s</a>", link, linkText);
    }

    public static String renderOutside(String linkText, String link) {
        return format("<a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"govuk-link\">%s</a>", link, linkText);
    }
}
