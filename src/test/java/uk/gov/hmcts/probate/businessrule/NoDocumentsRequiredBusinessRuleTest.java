package uk.gov.hmcts.probate.businessrule;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.NEVER_MARRIED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.WIDOWED_VALUE;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoDocumentsRequiredBusinessRuleTest {
    public static final String GRANT_TYPE_INTESTACY = "NoWill";
    public static final String IHT400421 = "IHT400421";
    public static final String IHT400 = "IHT400";
    public static final String SPOUSE_OR_CIVIL = "SpouseOrCivil";
    public static final String CHILD = "Child";
    public static final String CHILD_ADOPTED = "ChildAdopted";

    @InjectMocks
    private NoDocumentsRequiredBusinessRule underTest;

    private CaseData mockCaseData;

    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    private final DocumentLink mockDocumentLink = DocumentLink.builder().build();

    @Test
    void shouldBeApplicableWhenSpouseApplyingAndIHT400421PreEE() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenSpouseApplyingAndIHT400421PostEE() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstate(IHT400421)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndIHT400421NoDocumentsUploaded() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();

        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndIHT400421NoTIntestacyApplication() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();

        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedDivorced() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedDivorcedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedWidowed() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedWidowedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedJudiciallySeparated() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedJudiciallySeparatedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndIHT400421DeceasedNeverMarried() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndIHT400421DeceasedNeverMarriedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedDivorced() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedDivorcedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedWidowed() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedWidowedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedJudiciallySeparated() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedJudiciallySeparatedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedNeverMarried() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndIHT400DeceasedNeverMarried() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndIHT400421DeceasedNeverMarriedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormId(IHT400421)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndExceptedEstateNoDocumentsUploaded() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);

        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenSpouseApplyingAndExceptedEstateNotIntestacy() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);

        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenSpouseApplyingAndExceptedEstate() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(SPOUSE_OR_CIVIL)
            .deceasedMaritalStatus(MARRIED_VALUE)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedDivorced() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedDivorcedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedWidowed() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedWidowedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedJudiciallySeparated() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedJudiciallySeparatedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenChildApplyingAndExceptedEstateDeceasedNeverMarried() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenChildApplyingAndExceptedEstateDeceasedNeverMarriedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedDivorced() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedDivorcedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(DIVORCED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedWidowed() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedWidowedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(WIDOWED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedJudiciallySeparated() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableAdoptedChildApplyingAndExceptedEstateDeceasedJudiciallySeparatedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(JUDICIALLY_SEPARATED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedNeverMarried() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(NO)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableWhenAdoptedChildApplyingAndExceptedEstateDeceasedNeverMarriedApplicantHasSiblings() {
        mockCaseData = CaseData.builder()
            .solsWillType(GRANT_TYPE_INTESTACY)
            .solsLegalStatementUpload(mockDocumentLink)
            .ihtFormEstateValuesCompleted(NO)
            .solsApplicantRelationshipToDeceased(CHILD_ADOPTED)
            .deceasedMaritalStatus(NEVER_MARRIED_VALUE)
            .solsApplicantSiblings(YES)
            .build();
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}

