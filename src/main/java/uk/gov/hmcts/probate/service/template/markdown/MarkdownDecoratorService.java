package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    private final PA16FormBusinessRule pa16FormBusinessRule;
    public static final String PA16_FORM_TEXT =
        "[Give up probate administrator rights paper form](https://www.gov" 
            + ".uk/government/publications/form-pa16-give-up-probate-administrator-rights)";

    public String getPA16FormLabel(CaseData caseData) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return PA16_FORM_TEXT;
        }
        return "";
    }
}
