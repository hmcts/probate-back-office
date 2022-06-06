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

public class ApplicantSiblingsRuleTest {

    @InjectMocks
    private ApplicantSiblingsRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    public void shouldNeedChange() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("Yes");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeChild() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeChildAdopted() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeWithSpouseOrCivil() {
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodyApplicantSiblings", underTest.getConfirmationBodyMessageKey());
    }
}
