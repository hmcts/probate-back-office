package uk.gov.hmcts.probate.changerule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SpouseOrCivilRuleTest {

    @InjectMocks
    private SpouseOrCivilRule underTest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNotNeedChange() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeChild() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeChildAdopted() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("marriedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeNeverMarried() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("neverMarried");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeNeverWidowed() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("widowed");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeDivorcedCivilPartnership() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("divorcedCivilPartnership");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeJudicially() {
        when(caseDataMock.getDeceasedMaritalStatus()).thenReturn("judicially");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodySpouseOrCivil", underTest.getConfirmationBodyMessageKey());
    }
}
