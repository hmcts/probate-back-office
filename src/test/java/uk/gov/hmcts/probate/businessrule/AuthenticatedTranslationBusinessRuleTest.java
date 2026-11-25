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
class AuthenticatedTranslationBusinessRuleTest {

    @InjectMocks
    private AuthenticatedTranslationBusinessRule underTest;

    private CaseData mockCaseData;

    @Test
    void shouldBeApplicableForNonEnglishWill() {
        mockCaseData = CaseData.builder().englishWill(NO).build();
        assertTrue(underTest.isApplicable(mockCaseData));
    }

    @Test
    void shouldNotBeApplicableForEnglishWill() {
        mockCaseData = CaseData.builder().englishWill(YES).build();
        assertFalse(underTest.isApplicable(mockCaseData));
    }
}
