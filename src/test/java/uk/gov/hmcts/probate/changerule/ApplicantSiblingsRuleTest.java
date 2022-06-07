package uk.gov.hmcts.probate.changerule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ApplicantSiblingsRuleTest {

    @InjectMocks
    private ApplicantSiblingsRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNeedChange() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("Yes");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeChild() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeChildAdopted() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("ChildAdopted");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeWithSpouseOrCivil() {
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("SpouseOrCivil");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertEquals("stopBodyApplicantSiblings", underTest.getConfirmationBodyMessageKey());
    }
}
