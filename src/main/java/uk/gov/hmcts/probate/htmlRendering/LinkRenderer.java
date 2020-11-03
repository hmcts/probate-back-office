package uk.gov.hmcts.probate.htmlRendering;

import static java.lang.String.format;

public class LinkRenderer {
    public static String render(String linkText, String link, boolean openInNewTab) {
        final String target = openInNewTab ? "target=\"_blank\"" : null;
        return format("<a href=\"%s\" %s rel=\"noopener noreferrer\" class=\"govuk-link\">%s</a>", link, openInNewTab, linkText);
    }
}
