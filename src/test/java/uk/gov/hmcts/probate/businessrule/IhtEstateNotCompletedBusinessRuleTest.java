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

@ExtendWith(MockitoExtension.class)
class IhtEstateNotCompletedBusinessRuleTest {

    @InjectMocks
    private IhtEstateNotCompletedBusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForCompleted() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(YES).build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldBeApplicableForNotCompleted() {
        mockCaseData = CaseData.builder().ihtFormEstateValuesCompleted(NO).build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

}
