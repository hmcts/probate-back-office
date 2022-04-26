package uk.gov.hmcts.probate.businessrule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class PA16FormBusinessRuleTest {

    @InjectMocks
    private PA16FormBusinessRule underTest;

    @Mock
    private CaseData mockCaseData;
    
    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldBeApplicableForChildAdoptedNoApplicantSiblingsRenouncing() {
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        when(mockCaseData.getSolsSpouseOrCivilRenouncing()).thenReturn(YES);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldBeApplicableForChildNoApplicantSiblingsYesRenouncing() {
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        when(mockCaseData.getSolsSpouseOrCivilRenouncing()).thenReturn(YES);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNOTBeApplicableForNotChildAdoptedNoApplicantSiblingsNoRenouncing() {
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        when(mockCaseData.getSolsSpouseOrCivilRenouncing()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNOTBeApplicableForChildAdoptedYesApplicantSiblingsYesRenouncing() {
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(YES);
        when(mockCaseData.getSolsSpouseOrCivilRenouncing()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    public void shouldNOTBeApplicableForChildAdoptedNoApplicantSiblingsNoRenouncing() {
        when(mockCaseData.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");
        when(mockCaseData.getSolsApplicantSiblings()).thenReturn(NO);
        when(mockCaseData.getSolsSpouseOrCivilRenouncing()).thenReturn(NO);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

}