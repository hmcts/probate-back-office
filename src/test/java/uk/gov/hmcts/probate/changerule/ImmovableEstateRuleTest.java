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

public class ImmovableEstateRuleTest {

    @InjectMocks
    private ImmovableEstateRule underTest;

    @Mock
    private CaseData caseDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldNeedChange() {
        when(caseDataMock.getImmovableEstate()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldNotNeedChange() {
        when(caseDataMock.getImmovableEstate()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    public void shouldGetBodyMessageKey() {
        assertEquals("stopBodyNotImmovableEstate", underTest.getConfirmationBodyMessageKey());
    }
}
