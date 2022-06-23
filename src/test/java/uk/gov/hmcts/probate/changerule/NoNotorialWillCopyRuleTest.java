package uk.gov.hmcts.probate.changerule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class NoNotorialWillCopyRuleTest {

    @InjectMocks
    private NoNotorialWillCopyRule underTest;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldNeedChange() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("No");
        when(caseDataMock.getWillAccessNotarial()).thenReturn("No");

        assertTrue(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeWithWillAccessOriginal() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("Yes");
        when(caseDataMock.getWillAccessNotarial()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeWithWillNoAccessOriginal() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("No");
        when(caseDataMock.getWillAccessNotarial()).thenReturn("Yes");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldNotNeedChangeWithWillAccessOriginalNoNotirialCopy() {
        when(caseDataMock.getWillAccessOriginal()).thenReturn("Yes");
        when(caseDataMock.getWillAccessNotarial()).thenReturn("No");

        assertFalse(underTest.isChangeNeeded(caseDataMock));
    }

    @Test
    void shouldGetBodyMessageKey() {
        assertThrows(UnsupportedOperationException.class, () -> {
            underTest.getConfirmationBodyMessageKey();
        });
    }
}
