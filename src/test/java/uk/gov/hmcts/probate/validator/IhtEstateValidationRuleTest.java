package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class IhtEstateValidationRuleTest {

    private static final BigDecimal ESTATE_GROSS = BigDecimal.valueOf(1000000 * 100);
    private static final BigDecimal ESTATE_NET = BigDecimal.valueOf(900000 * 100);
    private static final BigDecimal ESTATE_NQV_SMALLER = BigDecimal.valueOf(300000 * 100);
    private static final BigDecimal ESTATE_NQV_BETWEEN = BigDecimal.valueOf(500000 * 100);
    private static final BigDecimal ESTATE_NQV_LARGER = BigDecimal.valueOf(700000 * 100);

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private IhtEstateValidationRule ihtEstateValidationRule;

    @BeforeEach
    void setUp() {
        ihtEstateValidationRule = new IhtEstateValidationRule(businessValidationMessageRetriever);
    }

    @Test
    void testValidateWithNoUnusedAllowanceError() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_BETWEEN, YES, null);

        assertValidationError(caseDetails, "unused iht message");
    }

    @Test
    void testValidateWithNoUnusedAllowanceNoError() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_BETWEEN, NO, null);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateWithUnusedAllowanceSetError() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_BETWEEN, YES, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateWithUnusedAllowanceSetNoError() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_BETWEEN, NO, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testMessageNQVLargerAnyYes() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_LARGER, YES, YES);

        assertValidationError(caseDetails, "expected iht message");
    }

    @Test
    void testMessageNQVLargerAnyNo() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_LARGER, YES, NO);

        assertValidationError(caseDetails, "expected iht message");
    }

    @Test
    void testMessageNQVLargerAnyNull() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_LARGER, YES, null);

        assertValidationError(caseDetails, "expected iht message");
    }

    @Test
    void testMessageNQVLargerNoNull() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_LARGER, NO, null);

        assertValidationError(caseDetails, "expected iht message");
    }

    @Test
    void testNoMessageNQVSmaller() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_SMALLER, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateNoMessageNoNvq() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, null, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateNoMessageNoEstateValues() {
        CaseDetails caseDetails = createCaseDetails(null, null, ESTATE_NQV_SMALLER, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateNoMessageNoProbateIhtValues() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, ESTATE_NET, ESTATE_NQV_SMALLER, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateNoMessageNoNetIhtValue() {
        CaseDetails caseDetails = createCaseDetails(ESTATE_GROSS, null, ESTATE_NQV_SMALLER, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testValidateNoMessageNoGrossIhtValue() {
        CaseDetails caseDetails = createCaseDetails(null, null, ESTATE_NQV_SMALLER, null, YES);

        assertDoesNotThrow(() -> ihtEstateValidationRule.validate(caseDetails));
    }

    @Test
    void testNvqBetweenValues() {
        assertTrue(ihtEstateValidationRule.isNqvBetweenValues(ESTATE_NQV_BETWEEN));
    }

    @Test
    void testNvqOutsideValues() {
        assertFalse(ihtEstateValidationRule.isNqvBetweenValues(ESTATE_NQV_SMALLER));
    }

    private CaseDetails createCaseDetails(
            BigDecimal gross,
            BigDecimal net,
            BigDecimal nqv,
            String hadLateSpouse,
            String unusedAllowanceClaimed) {

        CaseData caseData = CaseData.builder()
                .ihtEstateGrossValue(gross)
                .ihtEstateNetValue(net)
                .ihtEstateNetQualifyingValue(nqv)
                .deceasedHadLateSpouseOrCivilPartner(hadLateSpouse)
                .ihtUnusedAllowanceClaimed(unusedAllowanceClaimed)
                .build();

        return new CaseDetails(caseData, null, 1L);
    }

    private void assertValidationError(CaseDetails caseDetails, String expectedMessage) {
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(expectedMessage);

        BusinessValidationException exception = assertThrows(
                BusinessValidationException.class,
                () -> ihtEstateValidationRule.validate(caseDetails));

        assertEquals(expectedMessage, exception.getUserMessage());
    }
}
