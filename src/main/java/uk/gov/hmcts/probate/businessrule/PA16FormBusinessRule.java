package uk.gov.hmcts.probate.businessrule;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static java.util.Arrays.asList;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class PA16FormBusinessRule implements BusinessRule {

    public boolean isApplicable(CaseData caseData) {
        boolean childAdopted =
            asList("Child", "ChildAdopted").contains(caseData.getSolsApplicantRelationshipToDeceased());
        boolean noApplicantSiblings = NO.equals(caseData.getSolsApplicantSiblings());
        boolean hasCivilRenouncing = YES.equals(caseData.getSolsSpouseOrCivilRenouncing());

        return childAdopted && noApplicantSiblings && hasCivilRenouncing;
    }
}
