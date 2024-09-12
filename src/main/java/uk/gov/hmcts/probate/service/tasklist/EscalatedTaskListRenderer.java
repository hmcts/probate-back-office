package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.htmlrendering.ParagraphRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.htmltemplate.CaseEscalatedToRegistrarHtmlTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EscalatedTaskListRenderer extends NoTaskListCaseRenderer {

    public String renderBody(CaseDetails details) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate escalatedDate = details.getData().getEscalatedDate();

        return ParagraphRenderer.renderByReplace(CaseEscalatedToRegistrarHtmlTemplate.BASE_TEMPLATE
                .replaceFirst("<escalationDate>", escalatedDate == null ? "Unknown" : escalatedDate.format(dateFormat))
                .replaceFirst("<escalationDateWelsh>", escalatedDate == null
                        ? "Unknown" : escalatedDate.format(dateFormat))
                .replaceFirst("<numWeeks>", "6")
                .replaceFirst("<numWeeksWelsh>", "6"));
    }
}
