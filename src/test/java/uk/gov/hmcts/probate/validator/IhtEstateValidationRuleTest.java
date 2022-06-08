package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RunWith(MockitoJUnitRunner.class)
public class IhtEstateValidationRuleTest {
    private IhtEstateValidationRule ihtEstateValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    private static final BigDecimal ESTATE_GROSS = BigDecimal.valueOf(1000000 * 100);
    private static final BigDecimal ESTATE_NET = BigDecimal.valueOf(900000 * 100);
    private static final BigDecimal ESTATE_NQV_SMALLER = BigDecimal.valueOf(300000 * 100);
    private static final BigDecimal ESTATE_NQV_BETWEEN = BigDecimal.valueOf(500000 * 100);
    private static final BigDecimal ESTATE_NQV_LARGER = BigDecimal.valueOf(700000 * 100);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        this.ihtEstateValidationRule = new IhtEstateValidationRule(businessValidationMessageRetriever);
    }

    @Test
    public void testValidateWithNoUnusedAllowanceError() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_BETWEEN);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(YES);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("unused iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("unused iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testValidateWithNoUnusedAllowanceNoError() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_BETWEEN);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(NO);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateWithUnusedAllowanceSetError() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_BETWEEN);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(YES);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateWithUnusedAllowanceSetNoError() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_BETWEEN);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(NO);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testMessageNQVLargerAnyYes() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_LARGER);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(YES);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("expected iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("expected iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testMessageNQVLargerAnyNo() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_LARGER);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(YES);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(NO);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("expected iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("expected iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testMessageNQVLargerAnyNull() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_LARGER);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(YES);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(null);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("expected iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("expected iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testMessageNQVLargerNoNull() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_LARGER);
        when(caseDataMock.getDeceasedHadLateSpouseOrCivilPartner()).thenReturn(NO);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(null);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("expected iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("expected iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testNoMessageNQVSmaller() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_SMALLER);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoNvq() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoEstateValues() {
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_SMALLER);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoProbateIhtValues() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(ESTATE_NET);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_SMALLER);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoNetIhtValue() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(ESTATE_GROSS);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(null);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_SMALLER);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoGrossIhtValue() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(null);
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(ESTATE_NQV_SMALLER);
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testNvqBetweenValues() {
        assertTrue(ihtEstateValidationRule.isNqvBetweenValues(ESTATE_NQV_BETWEEN));
    }

    @Test
    public void testNvqOutsideValues() {
        assertFalse(ihtEstateValidationRule.isNqvBetweenValues(ESTATE_NQV_SMALLER));
    }
}
