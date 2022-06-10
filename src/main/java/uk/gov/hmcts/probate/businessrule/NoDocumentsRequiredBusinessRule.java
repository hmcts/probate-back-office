package uk.gov.hmcts.probate.businessrule;

import static java.util.Arrays.asList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.NO;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.NEVER_MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.WIDOWED_VALUE;

@Component
public class NoDocumentsRequiredBusinessRule implements BusinessRule {

    public static final String IHT400421 = "IHT400421";
    public static final String SPOUSE_OR_CIVIL = "SpouseOrCivil";
    public static final String CHILD = "Child";
    public static final String CHILD_ADOPTED = "ChildAdopted";

    private static final List<String> childOrAdoptedChildList =
        asList(CHILD, CHILD_ADOPTED);
    private static final List<String> notMarriedList =
        asList(DIVORCED_VALUE, WIDOWED_VALUE, JUDICIALLY_SEPARATED_VALUE, NEVER_MARRIED_VALUE);

    @Autowired
    ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public boolean isApplicable(CaseData caseData) {


        boolean isIntestacyApplication = GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType());

        boolean iht400421 = IHT400421.equals(caseData.getIhtFormEstate());

        boolean ihtFormEstateValuesNotCompleted = NO.equals(caseData.getIhtFormEstateValuesCompleted());

        boolean dodIsAfter2022 = exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath());
        boolean exceptedEstate = dodIsAfter2022 && ihtFormEstateValuesNotCompleted;

        boolean applicantIsSpouseorCivilPartnerOfDeceased = SPOUSE_OR_CIVIL.equals(caseData.getSolsApplicantRelationshipToDeceased());
        boolean deceasedIsMarriedOrCivilPartner = MARRIED_VALUE.equals(caseData.getDeceasedMaritalStatus());

        boolean applicantIsChildOrAdoptedChildOfDeceased = childOrAdoptedChildList.contains(caseData.getSolsApplicantRelationshipToDeceased());
        boolean deceasedHadNoOtherIssue = NO.equals(caseData.getSolsApplicantSiblings());
        boolean deceasedNotMarried = notMarriedList.contains(caseData.getDeceasedMaritalStatus());

        final List<CollectionMember<UploadDocument>> boDocumentsUploaded = caseData.getBoDocumentsUploaded();
        boolean legalStatementHasBeenUploaded = null != boDocumentsUploaded && !boDocumentsUploaded.isEmpty();

        return isIntestacyApplication
            && (iht400421 || exceptedEstate)
            && ((applicantIsSpouseorCivilPartnerOfDeceased && deceasedIsMarriedOrCivilPartner)
                || (applicantIsChildOrAdoptedChildOfDeceased && deceasedHadNoOtherIssue && deceasedNotMarried))
            && legalStatementHasBeenUploaded;
    }
}
