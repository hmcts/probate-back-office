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

public class ApplicantSiblingsRuleTest {

    @InjectMocks
    private ApplicantSiblingsRule underTest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNeedChange() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("Yes");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Child");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChange() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeWithSpouseOrCivil() {
        when(caseDataMock.getSolsApplicantSiblings()).thenReturn("Yes");
        when(caseDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn("Spouse or civil partner");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodyApplicantSiblings", underTest.getConfirmationBodyMessageKey());
    }
}
