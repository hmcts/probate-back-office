package uk.gov.hmcts.probate.businessrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

@ExtendWith(MockitoExtension.class)
class IhtEstate207BusinessRuleTest {

    @InjectMocks
    private IhtEstate207BusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForCompletedAndIHT207() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(YES).ihtFormEstate(IHT207_VALUE).build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotCompletedAndIHT207() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(NO).ihtFormEstate(IHT207_VALUE).build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForNotCompletedAndNotIHT207() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(NO).ihtFormEstate(IHT400421_VALUE).build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForCompletedAndNotIHT207() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(YES).ihtFormEstate(IHT400421_VALUE).build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}
