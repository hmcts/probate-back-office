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

class SolsExecutorRuleTest {

    @InjectMocks
    private SolsExecutorRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNeedChangeSolIsExecAndIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeSolIsExecAndIntestacyYesNo() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeSolIsExecAndIntestacyNoYes() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeSolIsExecAndIntestacyNoNo() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeSolIsExecAndAdmonWill() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeftAnnexed");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeIfGrantOfProbate() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeft");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeIfSolIsNOTExecAndIntestacy() {
        when(caseDataMock.getSolsWillType()).thenReturn("NoWill");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeIfSolIsNOTExecAndAdmonWill() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeftAnnexed");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNeedChangeIfSolIsNOTExecAndAdmonWillYesNoPowerReserved() {
        when(caseDataMock.getSolsWillType()).thenReturn("WillLeftAnnexed");
        when(caseDataMock.getSolsSolicitorIsExec()).thenReturn("Yes");
        when(caseDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(caseDataMock.getSolsSolicitorNotApplyingReason()).thenReturn("PowerReserved");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertEquals("stopBodySolsExecutor", underTest.getConfirmationBodyMessageKey());
    }
}
