package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.NoWillRule;
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
    private DomicilityRule domicilityRule;
    @Mock
    private ExecutorsRule executorsStateRule;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);

        underTest = new StateChangeService(noWillRule, noOriginalWillRule, domicilityRule, executorsStateRule);
    }

    @Test
    public void shouldChangeStateForAnyRuleValid() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(true);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForWillDetails() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldNotChangeStateForOriginalWillDetails() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldChangeStateForDomicilityRuleValid() {
        when(domicilityRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForDomicility() {
        when(domicilityRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldChangeStateForExecutorsRuleValid() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(true);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals("Stopped", newState.get());
    }

    @Test
    public void shouldNotChangeStateForExecutors() {
        when(executorsStateRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals(Optional.empty(), newState);
    }

    @Test
    public void shouldNotChangeStateForAllRulesInvalid() {
        when(noWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);
        when(noOriginalWillRule.isChangeNeeded(caseDataMock)).thenReturn(false);

        Optional<String> newState = underTest.getChangedStateForCaseUpdate(caseDataMock);

        assertEquals(false, newState.isPresent());
    }

}
