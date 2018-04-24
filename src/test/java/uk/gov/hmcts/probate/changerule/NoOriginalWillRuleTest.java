package uk.gov.hmcts.probate.changerule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NoOriginalWillRuleTest {


    @InjectMocks
    private NoOriginalWillRule undertest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNeedChange() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("No");
        boolean changeNeeded = undertest.isChangeNeeded(caseDataMock);

        assertEquals(true, changeNeeded);
    }

    @Test
    public void shouldNotNeedChange() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("Yes");

        assertEquals(false, undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {

        assertEquals("willStopBodyNoOriginal", undertest.getConfirmationBodyMessageKey());
    }

}
