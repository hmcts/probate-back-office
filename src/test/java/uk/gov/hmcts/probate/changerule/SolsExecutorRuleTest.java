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
    public void shouldNeedChangeSolIsExecAndIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNeedChangeSolIsExecAndAdmonWill() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeftAnnexed");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeIfGrantOfProbate() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeft");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeIfSolIsNOTExecAndIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChangeIfSolIsNOTExecAndAdmonWill() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeftAnnexed");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodySolsExecutor", underTest.getConfirmationBodyMessageKey());
    }
}
