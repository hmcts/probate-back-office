package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class PA16FormBusinessRuleTest {

    @InjectMocks
    private PA16FormBusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForChildAdoptedNoApplicantSiblingsRenouncing() {
        mockCaseData = CaseData.builder()
            .solsApplicantRelationshipToDeceased("ChildAdopted")
            .solsApplicantSiblings(NO)
            .solsSpouseOrCivilRenouncing(YES)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForChildNoApplicantSiblingsYesRenouncing() {
        mockCaseData = CaseData.builder()
            .solsApplicantRelationshipToDeceased("Child")
            .solsApplicantSiblings(NO)
            .solsSpouseOrCivilRenouncing(YES)
            .build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForNotChildAdoptedNoApplicantSiblingsNoRenouncing() {
        mockCaseData = CaseData.builder()
            .solsApplicantRelationshipToDeceased("SpouseOrCivil")
            .solsApplicantSiblings(NO)
            .solsSpouseOrCivilRenouncing(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForChildAdoptedYesApplicantSiblingsYesRenouncing() {
        mockCaseData = CaseData.builder()
            .solsApplicantRelationshipToDeceased("ChildAdopted")
            .solsApplicantSiblings(YES)
            .solsSpouseOrCivilRenouncing(YES)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNOTBeApplicableForChildAdoptedNoApplicantSiblingsNoRenouncing() {
        mockCaseData = CaseData.builder()
            .solsApplicantRelationshipToDeceased("ChildAdopted")
            .solsApplicantSiblings(NO)
            .solsSpouseOrCivilRenouncing(NO)
            .build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

}
