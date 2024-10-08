package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

class IhtEstateNotCompletedBusinessRuleTest {

    @InjectMocks
    private IhtEstateNotCompletedBusinessRule underTest;

    @Mock
    private CaseData mockCaseData;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldBeApplicableForCompleted() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForNotCompleted() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

}
