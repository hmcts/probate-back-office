package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.ParagraphRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseEscalatedToRegistrarHtmlTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EscalatedTaskListRenderer extends NoTaskListRenderer {

    public String renderBody(CaseDetails details) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate escalatedDate = details.getData().getEscalatedDate();

        return ParagraphRenderer.renderByReplace(CaseEscalatedToRegistrarHtmlTemplate.baseTemplate
                .replaceFirst("<escalationDate>", escalatedDate == null ? "Unknown" : escalatedDate.format(dateFormat))
                .replaceFirst("<numWeeks>", "6"));
    }
}
