package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.TCResolutionLodgedWithApplicationRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.TC_RESOLUTION_LODGED_WITH_APP;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final TCResolutionLodgedWithApplicationRule tcResolutionLodgedWithApplicationRule;

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

    public String getTcResolutionFormLabel(CaseData caseData) {
        if (tcResolutionLodgedWithApplicationRule.isApplicable(caseData)) {
            return "\n*   " + TC_RESOLUTION_LODGED_WITH_APP;
        }
        return "";
    }
}
