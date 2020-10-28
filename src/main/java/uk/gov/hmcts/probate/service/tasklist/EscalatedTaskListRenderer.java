package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseEscalatedToRegistrarHtmlTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class EscalatedTaskListRenderer extends NoTaskListRenderer {
    public String renderBody(CaseDetails details) {

        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        LocalDate escalatedDate = details.getData().getEscalatedDate();
        return CaseEscalatedToRegistrarHtmlTemplate.baseTemplate.replaceFirst("<escalationDate>", escalatedDate == null ? "Unknown" : dateFormat.format(escalatedDate));
    }
}
