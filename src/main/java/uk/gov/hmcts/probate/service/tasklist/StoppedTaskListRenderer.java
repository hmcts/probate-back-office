package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlRendering.UnorderedListRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmlTemplate.CaseStoppedHtmlTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StoppedTaskListRenderer extends NoTaskListRenderer {

    public String renderBody(CaseDetails details) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate stoppedDate = details.getData().getGrantStoppedDate();

        return ParagraphRenderer.renderByReplace(CaseStoppedHtmlTemplate.baseTemplate)
                .replaceFirst("<stopDate>", stoppedDate == null ? "Unknown" : stoppedDate.format(dateFormat))
                .replaceFirst("<caseStopReasonsList>", UnorderedListRenderer.render(CaseStoppedHtmlTemplate.caseStopReasons))
                .replaceFirst("<numWeeks>", "4");
    }
}
