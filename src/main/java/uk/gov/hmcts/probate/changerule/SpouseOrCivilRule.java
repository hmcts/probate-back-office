package uk.gov.hmcts.probate.changerule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Component
public class SpouseOrCivilRule implements ChangeRule {
    private static final String MESSAGE_KEY = "stopBodySpouseOrCivil";
    private static final String MARITAL_STATUS_MARRIED = "marriedCivilPartnership";
    private static final String RELATIONSHIP_SPOUSE_CIVIL = "SpouseOrCivil";

    @Override
    public boolean isChangeNeeded(CaseData caseData) {
        return (caseData.getDeceasedMaritalStatus() != MARITAL_STATUS_MARRIED && caseData.getSolsApplicantRelationshipToDeceased() == RELATIONSHIP_SPOUSE_CIVIL);
    }

    @Override
    public String getConfirmationBodyMessageKey() {
        return MESSAGE_KEY;
    }
}
