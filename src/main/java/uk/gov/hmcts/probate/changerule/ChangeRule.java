package uk.gov.hmcts.probate.changerule;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

public interface ChangeRule {

    boolean isChangeNeeded(CaseData caseData);

    String getConfirmationBodyMessageKey();
}
