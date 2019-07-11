package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Component
public class GrantTypeRule implements ChangeRule {
    private static final String PROBATE_KEY = "WillLeft";
    private static final String INTESTACY_KEY = "NoWill";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return !PROBATE_KEY.equals(caseData.getSolsWillType());
    }

    public boolean isIntestacy(CaseData caseData) {
        return INTESTACY_KEY.equals(caseData.getSolsWillType());
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        throw new UnsupportedOperationException();
    }
}
