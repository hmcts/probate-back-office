package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlRendering.UnorderedListRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseEscalatedToRegistrarHtmlTemplate;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseStoppedHtmlTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
// import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

public class StoppedTaskListRenderer extends NoTaskListRenderer {
    public String renderBody(CaseDetails details) {

        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        LocalDate stoppedDate = details.getData().getGrantStoppedDate();

        return new ParagraphRenderer().render(CaseStoppedHtmlTemplate.baseTemplate)
                .replaceFirst("<stopDate>", stoppedDate == null ? "Unknown" : dateFormat.format(stoppedDate))
                .replaceFirst("<caseStopReasonsList>", new UnorderedListRenderer().render(CaseStoppedHtmlTemplate.caseStopReasons))
                .replaceFirst("<numWeeks>", "4"); // TODO - pick up from config (env var) so we can change without code change
    }
}
