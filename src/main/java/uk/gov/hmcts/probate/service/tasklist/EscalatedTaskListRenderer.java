package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.ParagraphRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseEscalatedToRegistrarHtmlTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class EscalatedTaskListRenderer extends NoTaskListRenderer {
    protected String renderBody(CaseDetails details) {
        // TODO implement getEscalationDate and get added to model
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        LocalDate stoppedDate = details.getData().getGrantStoppedDate();
        return new ParagraphRenderer().render(CaseEscalatedToRegistrarHtmlTemplate.baseTemplate)
                .replaceFirst("<escalationDate>", stoppedDate == null ? "Unknown" : dateFormat.format(stoppedDate))
                .replaceFirst("<numWeeks>", "6"); // TODO - pick up from config (env var) so we can change without code change
    }
}
