package uk.gov.hmcts.probate.service.tasklist;


import uk.gov.hmcts.probate.model.ccd.tasklist.Alert;

import static java.lang.String.format;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TaskListRenderer {

    public String render(Alert alert) {
        final List<String> lines = new LinkedList<>();

        lines.add("<div class='width-50'>");

        lines.addAll(renderAlert(alert));

        lines.add("</div>");

        return String.join("\n\n", lines);
    }

    private List<String> renderAlert(Alert alert) {
        final List<String> lines = new LinkedList<>();

        String insetText = renderInset(alert.getInset());
        String headerText = renderHeader();
        String bodyText = renderBodyText(alert.getBody(), alert.getList(), alert.getDate());

        lines.add(insetText);
        lines.add(headerText);
        lines.add(bodyText);

        return lines;
    }

    private String renderInset(String text) {
        return format("<div class=\"govuk-inset-text\">%s</div>", text);
    }

    private String renderHeader() {
        return "## What happens next";
    }

    private String renderBodyText(String text, Optional<List<String>> list, String date) {
        list.ifPresent(strings -> text.replaceAll("/(<list>)/g", renderBodyUnorderedList(strings)));

        text.replaceAll("/(<date>)/g", date);

        return format("<p class=\"govuk-body-s\">%s</p>", text);
    }

    private String renderBodyUnorderedList(List<String> listItems) {
        StringBuilder sb = new StringBuilder("<ul class=\"govuk-list govuk-list--bullet\">");

        for (String item : listItems) {
            sb.append(format("<li>%s</li>\n", item));
        }

        sb.append("</ul>");

        return sb.toString();
    }

}
