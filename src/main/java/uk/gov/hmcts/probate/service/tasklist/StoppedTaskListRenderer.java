package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlrendering.UnorderedListRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseStoppedHtmlTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StoppedTaskListRenderer extends NoTaskListCaseRenderer {

    protected String renderBody(CaseDetails details) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate stoppedDate = details.getData().getGrantStoppedDate();

        return ParagraphRenderer.renderByReplace(CaseStoppedHtmlTemplate.BASE_TEMPLATE)
                .replaceFirst("<stopDate>", stoppedDate == null ? "Unknown" : stoppedDate.format(dateFormat))
                .replaceFirst("<caseStopReasonsList>", UnorderedListRenderer.render(CaseStoppedHtmlTemplate.CASE_STOP_REASONS))
                .replaceFirst("<numWeeks>", "4");
    }
}
