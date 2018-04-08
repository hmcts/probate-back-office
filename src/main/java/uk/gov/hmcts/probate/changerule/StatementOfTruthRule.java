package uk.gov.hmcts.probate.changerule;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Component
public class StatementOfTruthRule implements ChangeRule {


    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return (YES.equals(caseData.getSolsSOTNeedToUpdate()));
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        throw new NotImplementedException();
    }
}
