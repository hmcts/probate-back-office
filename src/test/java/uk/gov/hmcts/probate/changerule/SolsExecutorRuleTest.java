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

public class SolsExecutorRuleTest {

    @InjectMocks
    private SolsExecutorRule underTest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNeedChangeMainApplicant() {
        when(caseDataMock.getSolsSolicitorIsMainApplicant()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeNoMainApplicant() {
        when(caseDataMock.getSolsSolicitorIsApplyingExec()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsMainApplicant()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChange() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeft");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodySolsExecutor", underTest.getConfirmationBodyMessageKey());
    }
}
