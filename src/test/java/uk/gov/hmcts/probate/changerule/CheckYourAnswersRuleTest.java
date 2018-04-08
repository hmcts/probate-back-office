package uk.gov.hmcts.probate.changerule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CheckYourAnswersRuleTest {
    @InjectMocks
    private CheckYourAnswersRule undertest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNeedChange() {
        when(caseDataMock.getSolsCYANeedToUpdate()).thenReturn("Yes");
        when(caseDataMock.getSolsCYAStateTransition()).thenReturn("newState");

        assertEquals(true, undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotChangeState() {
        when(caseDataMock.getSolsCYAStateTransition()).thenReturn("No");

        assertEquals(false, undertest.isChangeNeeded(caseDataMock));
    }


}
