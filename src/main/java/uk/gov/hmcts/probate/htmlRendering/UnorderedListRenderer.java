package uk.gov.hmcts.probate.htmlRendering;

import java.util.List;

import static java.lang.String.format;

public class UnorderedListRenderer {
    private static final String openTag = "<ul class=\"govuk-list govuk-list--bullet\">";
    private static final String closeTag = "</ul>";

    public static String render(List<String> listItems) {
        StringBuilder sb = new StringBuilder(openTag);
        for (String item : listItems) {
            sb.append(format("<li>%s</li>\n", item));
        }
        sb.append(closeTag);
        return sb.toString();
    }

    public static String renderByReplace(String html) {
        return html == null ? null :
                html.replaceAll("<ul>", openTag);
    }
}
