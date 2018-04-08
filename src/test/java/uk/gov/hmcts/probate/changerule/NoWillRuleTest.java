package uk.gov.hmcts.probate.changerule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NoWillRuleTest {

    @InjectMocks
    private NoWillRule undertest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldChangeState() {
        when(caseDataMock.getWillExists()).thenReturn("No");

        assertEquals(true, undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotChangeState() {
        when(caseDataMock.getWillExists()).thenReturn("Yes");

        assertEquals(false, undertest.isChangeNeeded(caseDataMock));
    }

}
