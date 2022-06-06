package uk.gov.hmcts.probate.changerule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UpdateApplicationRuleTest {

    @InjectMocks
    private UpdateApplicationRule undertest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldChangeState() {
        when(caseDataMock.getSolsSOTNeedToUpdate()).thenReturn("Yes");

        assertTrue(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotChangeState() {
        when(caseDataMock.getSolsSOTNeedToUpdate()).thenReturn("No");

        assertFalse(undertest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertThrows(UnsupportedOperationException.class, () -> {
            undertest.getConfirmationBodyMessageKey();
        });
    }
}
