package uk.gov.hmcts.probate.changerule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SpouseOrCivilRuleTest {

    @InjectMocks
    private SpouseOrCivilRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNotNeedChange() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeChild() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeChildAdopted() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeNeverMarried() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("neverMarried");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeNeverWidowed() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("widowed");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeDivorcedCivilPartnership() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("divorcedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeJudicially() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("judicially");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertEquals("stopBodySpouseOrCivil", underTest.getConfirmationBodyMessageKey());
    }
}
