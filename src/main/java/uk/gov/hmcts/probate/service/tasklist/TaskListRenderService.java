package uk.gov.hmcts.probate.service.tasklist;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.tasklist.Alert;

import static java.lang.String.format;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskListRenderService {

    public String render(Alert alert) {
        final List<String> lines = new LinkedList<>();

        lines.add("<div class='width-50'>");

        lines.add(renderHeader("Case progress"));

        lines.addAll(renderAlert(alert));

        lines.add("</div>");

        return String.join("\n\n", lines);
    }

    private List<String> renderAlert(Alert alert) {
        final List<String> lines = new LinkedList<>();

        String insetText = renderInset(alert.getInset());
        String headerText = renderHeader("What happens next");
        String bodyText = renderBodyText(alert.getBody(), alert.getList(), alert.getDate());

        lines.add(insetText);
        lines.add(headerText);
        lines.add(bodyText);

        return lines;
    }

    private String renderInset(String text) {
        return format("<div class=\"govuk-inset-text govuk-!-font-weight-bold govuk-!-font-size-48\">%s</div>", text);
    }

    private String renderHeader(String text) {
        return format("## %s", text);
    }

    private String renderBodyText(String text, Optional<List<String>> list, String date) {
        String newBodyText = text;

        if (list.isPresent()) {
            newBodyText = newBodyText.replaceAll("<list>", renderBodyUnorderedList(list.get()));
        }

        newBodyText = newBodyText.replaceAll("<date>", date);

        return format("<p class=\"govuk-body-s\">%s</p>", newBodyText);
    }

    private String renderBodyUnorderedList(List<String> listItems) {
        StringBuilder sb = new StringBuilder("<ul class=\"govuk-list govuk-list--bullet\">\n");

        for (String item : listItems) {
            sb.append(format("<li>%s</li>\n", item));
        }

        sb.append("</ul>");

        return sb.toString();
    }

}
