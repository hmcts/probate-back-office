package uk.gov.hmcts.probate.htmlRendering;

import java.util.List;

import static java.lang.String.format;

public class UnorderedListRenderer {
    public String render(List<String> listItems) {
        StringBuilder sb = new StringBuilder("<ul class=\"govuk-list govuk-list--bullet\">");

        for (String item : listItems) {
            sb.append(format("<li>%s</li>\n", item));
        }

        sb.append("</ul>");

        return sb.toString();
    }
}
