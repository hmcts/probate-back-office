package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class UpdateApplicationRule implements ChangeRule {

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return YES.equals(caseData.getSolsSOTNeedToUpdate());
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        throw new UnsupportedOperationException();
    }
}
