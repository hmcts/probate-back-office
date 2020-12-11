package uk.gov.hmcts.probate.htmlRendering;

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class UnorderedListRenderer {
    private static final String OPEN_TAG = "<ul class=\"govuk-list govuk-list--bullet\">";
    private static final String CLOSE_TAG = "</ul>";

    private UnorderedListRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String render(List<String> listItems) {
        StringBuilder sb = new StringBuilder(OPEN_TAG);
        sb.append("\n");
        for (String item : listItems) {
            sb.append(format("<li>%s</li>\n", item));
        }
        sb.append(CLOSE_TAG);
        sb.append("\n");
        return sb.toString();
    }

    public static String renderByReplace(String html) {
        return html == null ? null :
                html.replaceAll(Pattern.quote("<ul>"), OPEN_TAG);
    }
}
