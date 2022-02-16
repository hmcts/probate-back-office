package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static java.util.Arrays.asList;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING;


@Component
public class PA17FormBusinessRule implements BusinessRule {
    private static final List<String> applicableTitleAndClearingTypes =
        asList(TITLE_AND_CLEARING_PARTNER_SUCCESSOR_OTHERS_RENOUNCING,
        TITLE_AND_CLEARING_PARTNER_OTHERS_RENOUNCING,
        TITLE_AND_CLEARING_PARTNER_SUCC_ALL_RENOUNCING,
        TITLE_AND_CLEARING_PARTNER_ALL_RENOUNCING);
    
    public boolean isApplicable(CaseData caseData) {
        return applicableTitleAndClearingTypes.contains(caseData.getTitleAndClearingType());
    }
}
