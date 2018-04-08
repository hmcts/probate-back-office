package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.changerule.CheckYourAnswersRule;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.NoWillRule;
import uk.gov.hmcts.probate.changerule.StatementOfTruthRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
public class StateChangeServiceTest {

    @InjectMocks
    private StateChangeService underTest;

    @Mock
    private NoWillRule noWillRule;
    @Mock
    private NoOriginalWillRule noOriginalWillRule;
    @Mock
    private CheckYourAnswersRule checkYourAnswersRule;
    @Mock
    private DomicilityRule domicilityRule;
    @Mock
    private ExecutorsRule executorsStateRule;
    @Mock
    private StatementOfTruthRule statementOfTruthStateChangeRule;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);

        underTest = new StateChangeService(noWillRule, noOriginalWillRule, checkYourAnswersRule,
            statementOfTruthStateChangeRule, domicilityRule, executorsStateRule);
    }

    @Test
    public void shouldChangeStateForAnyRuleValid() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForWillDetails(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForWillDetails() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForWillDetails(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldNotChangeStateForOriginalWillDetails() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForWillDetails(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldChangeStateForCYARuleValid() {
        when(caseDataMock.getSolsCYAStateTransition()).thenReturn("stateTransition");
        when(checkYourAnswersRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCheckYourAnswers(caseDataMock);

        assertEquals("stateTransition", newState.get());
    }

    @Test
    public void shouldNotChangeStateForCYA() {
        when(checkYourAnswersRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCheckYourAnswers(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForDomicilityRuleValid() {
        when(domicilityRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForDomicility(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForDomicility() {
        when(domicilityRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForDomicility(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForExecutorsRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForExecutors(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForExecutors() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForExecutors(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForSOTValid() {
        when(caseDataMock.getSolsSOTStateTransition()).thenReturn("newState");
        when(statementOfTruthStateChangeRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForStatementOfTruth(caseDataMock);

        assertEquals("newState", newState.get());
    }

    @Test
    public void shouldNotChangeStateForSOT() {
        when(statementOfTruthStateChangeRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForStatementOfTruth(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldNotChangeStateForAllRulesInvalid() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForWillDetails(caseDataMock);

        assertEquals(false, newState.isPresent());
    }

}
