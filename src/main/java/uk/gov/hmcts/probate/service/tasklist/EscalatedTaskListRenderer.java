package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseEscalatedToRegistrarHtmlTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EscalatedTaskListRenderer extends NoTaskListRenderer {
    public String renderBody(CaseDetails details) {
        // TODO implement getEscalationDate and get added to model
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        return CaseEscalatedToRegistrarHtmlTemplate.baseTemplate.replaceFirst("<escalationDate>", dateFormat.format(details.getData().getGrantStoppedDate()));
    }
}
