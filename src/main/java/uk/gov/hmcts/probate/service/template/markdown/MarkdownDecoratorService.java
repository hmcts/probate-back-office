package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    private final PA16FormBusinessRule pa16FormBusinessRule;

    public String getPA16FormLabel(CaseData caseData) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return "<a href=\"" + PA16_FORM_URL+ "\" target='blank'>" + PA16_FORM_TEXT + "</a>";
        }
        return "";
    }
}
