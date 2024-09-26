package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.htmlrendering.UnorderedListRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseStoppedHtmlTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StoppedTaskListRenderer extends NoTaskListCaseRenderer {

    @Value("${grand_delay.number_of_weeks}")
    private String grandDelayNumberOfWeeks;

    protected String renderBody(CaseDetails details) {

        Locale welshLocale = new Locale("cy", "GB");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter welshDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", welshLocale);
        LocalDate stoppedDate = details.getData().getGrantStoppedDate();

        return ParagraphRenderer.renderByReplace(CaseStoppedHtmlTemplate.BASE_TEMPLATE)
                .replaceFirst("<stopDate>", stoppedDate == null ? "Unknown" : stoppedDate.format(dateFormat))
                .replaceFirst("<stopDateWelsh>", stoppedDate == null ? "Unknown" : stoppedDate.format(welshDateFormat))
                .replaceFirst("<caseStopReasonsList>",
                        UnorderedListRenderer.render(CaseStoppedHtmlTemplate.CASE_STOP_REASONS))
                .replaceFirst("<caseStopReasonsListWelsh>",
                        UnorderedListRenderer.render(CaseStoppedHtmlTemplate.CASE_STOP_REASONS_WELSH))
                .replaceFirst("<numWeeks>", grandDelayNumberOfWeeks)
                .replaceFirst("<numWeeksWelsh>", grandDelayNumberOfWeeks);
    }
}
