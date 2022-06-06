package uk.gov.hmcts.probate.changerule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class MinorityInterestRuleTest {

    @InjectMocks
    private MinorityInterestRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNeedChange() {
        when(caseDataMock.getSolsMinorityInterest()).thenReturn("Yes");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChange() {
        when(caseDataMock.getSolsMinorityInterest()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertEquals("stopBodyMinorityInterest", underTest.getConfirmationBodyMessageKey());
    }
}
