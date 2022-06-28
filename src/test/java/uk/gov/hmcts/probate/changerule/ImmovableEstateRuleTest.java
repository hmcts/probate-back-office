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

class ImmovableEstateRuleTest {

    @InjectMocks
    private ImmovableEstateRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNeedChange() {
        when(caseDataMock.getImmovableEstate()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChange() {
        when(caseDataMock.getImmovableEstate()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertEquals("stopBodyNotImmovableEstate", underTest.getConfirmationBodyMessageKey());
    }
}
