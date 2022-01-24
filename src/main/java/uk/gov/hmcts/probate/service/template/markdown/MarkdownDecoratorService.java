package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;

    public String getPA15FormLabel(CaseData caseData) {
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            return "\n*   <a href=\"" + PA15_FORM_URL + "\" target=\"_blank\">" + PA15_FORM_TEXT + "</a>";
        }
        return "";
    }

    public String getPA16FormLabel(CaseData caseData) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return "\n*   <a href=\"" + PA16_FORM_URL + "\" target=\"_blank\">" + PA16_FORM_TEXT + "</a>";
        }
        return "";
    }

    public String getPA17FormLabel(CaseData caseData) {
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            return "\n*   <a href=\"" + PA17_FORM_URL + "\" target=\"_blank\">" + PA17_FORM_TEXT + "</a>";
        }
        return "";
    }
}
