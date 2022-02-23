package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
public class NoNotorialWillCopyRule implements ChangeRule {

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return NO.equals(caseData.getWillAccessOriginal()) && NO.equals(caseData.getWillAccessNotarial());
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        throw new UnsupportedOperationException();
    }
}
