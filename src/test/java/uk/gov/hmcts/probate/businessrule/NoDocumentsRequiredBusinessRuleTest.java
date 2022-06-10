package uk.gov.hmcts.probate.businessrule;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.NEVER_MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.WIDOWED_VALUE;

class NoDocumentsRequiredBusinessRuleTest {
    public static final String GRANT_TYPE_INTESTACY = "NoWill";
    public static final String IHT400421 = "IHT400421";
    public static final String SPOUSE_OR_CIVIL = "SpouseOrCivil";
    public static final String CHILD = "Child";
    public static final String CHILD_ADOPTED = "ChildAdopted";

    @InjectMocks
    private NoDocumentsRequiredBusinessRule underTest;

    @Mock
    private CaseData mockCaseData;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @Mock
    private List<CollectionMember<UploadDocument>> boDocumentsUploaded;

    @BeforeEach
    public void setup() {
        openMocks(this);
        when(mockCaseData.getSolsWillType()).thenReturn(GRANT_TYPE_INTESTACY);
        when(mockCaseData.getBoDocumentsUploaded()).thenReturn(boDocumentsUploaded);
        when(boDocumentsUploaded.isEmpty()).thenReturn(false);
    }

    @Test
    void shouldBeApplicableWhenSpouseApplyingAndIHT400421() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndIHT400421NoDocumentsUploaded() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        when(boDocumentsUploaded.isEmpty()).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndIHT400421NoTIntestacyApplication() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        when(mockCaseData.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedDivorced() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedDivorcedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedWidowed() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedWidowedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedJudiciallySeparated() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedJudiciallySeparatedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedNeverMarried() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedNeverMarriedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedDivorced() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedDivorcedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedWidowed() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedWidowedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedJudiciallySeparated() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedJudiciallySeparatedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedNeverMarried() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedNeverMarriedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndExceptedEstateNoDocumentsUploaded() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        when(boDocumentsUploaded.isEmpty()).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndExceptedEstateNotIntestacy() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        when(mockCaseData.getSolsWillType()).thenReturn(GRANT_TYPE_PROBATE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenSpouseApplyingAndExceptedEstate() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(SPOUSE_OR_CIVIL);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(MARRIED_VALUE);
        assertTrue(underTest.isApplicable(mockCaseData));
    }
    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedDivorced() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedDivorcedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedWidowed() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedWidowedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedJudiciallySeparated() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedJudiciallySeparatedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedNeverMarried() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedNeverMarriedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedDivorced() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedDivorcedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedWidowed() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedWidowedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(WIDOWED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedJudicallySeparated() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedJudicallySeparatedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedNeverMarried() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedNeverMarriedApplicantHasSiblings() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn(CHILD_ADOPTED);
        when(mockCaseData.getDeceasedMaritalStatus()).thenReturn(NEVER_MARRIED_VALUE);
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}


//  GIVEN THAT a probate practitioner is making an intestacy application
//              "solsWillType": "NoWill",
//  AND
//
//  (
//      the IHT 400 and IHT421 option is chosen( pre/post 2022)
//              "ihtFormEstate": "IHT400421",
//
//	    OR
//
//      the DOD is on or after 01.01.2022
//      AND I have selected 'No' to Did you complete IHT forms to report the estate's values? ( excepted estate)
//              "deceasedDateOfDeath": onOrAfer "2022-02-02" AND  "ihtFormEstateValuesCompleted": "No",
//  )
//
//  AND
//
//  (
//      "AND the option 'spouse or civil partner' is selected for the question What is the applicant's relationship to the deceased and they select 'married or in a civil partnership' in an answer to 'what was the marital status of the deceased at the date of death'
//              "solsApplicantRelationshipToDeceased": "SpouseOrCivil" AND   "deceasedMaritalStatus": "marriedCivilPartnership",
//
//      OR they select 'child' in answer to 'what is the applicant's relationship to the deceased' and  they choose 'no' in answer to 'does the deceased have any other issue' and then they choose either 'divorced or civil partnership has dissolved', 'widowed, their spouse or civil partner died before them', 'judicially separated' or 'never married' in answer to 'what was the marital status of the deceased at the date of death'
//              "solsApplicantRelationshipToDeceased": "Child" AND "solsApplicantSiblings": "No" AND   "deceasedMaritalStatus": ("divorcedCivilPartnership" OR widowed OR judicially OR neverMarried)
//
//      OR they select 'adopted child' in answer to 'what is the applicant's relationship to the deceased' and  they choose 'no' OR 'yes' in answer to 'did the adoption take place in England or Wales? and they answer 'no' in answer to 'does the deceased have any other issue' and then they choose either 'divorced or civil partnership has dissolved', 'widowed, their spouse or civil partner died before them', 'judicially separated' or 'never married' in answer to 'what was the marital status of the deceased at the date of death"
//              "solsApplicantRelationshipToDeceased": "ChildAdopted"  AND "solsApplicantSiblings": "No" AND   "deceasedMaritalStatus": ("divorcedCivilPartnership" OR widowed OR judicially OR neverMarried)
//  )
//
//
//  AND the legal statement has been uploaded
//              "boDocumentsUploaded": !== null and "boDocumentsUploaded.size>0
