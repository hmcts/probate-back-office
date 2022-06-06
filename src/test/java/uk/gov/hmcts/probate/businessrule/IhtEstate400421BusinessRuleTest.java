package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

class IhtEstate400421BusinessRuleTest {

    @InjectMocks
    private IhtEstate400421BusinessRule underTest;

    @Mock
    private CaseData mockCaseData;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldBeApplicableForCompletedAndIHT400421() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421_VALUE);
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotCompletedAndIHT400421() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT400421_VALUE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotCompletedAndNotIHT400421() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForCompletedAndNotIHT400421() {
        when(mockCaseData.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(mockCaseData.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}
