package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_NET_GREATER_THAN_GROSS;

@RunWith(MockitoJUnitRunner.class)
public class IHTValidationRuleTest {

    private static final BigDecimal HIGHER_VALUE = BigDecimal.valueOf(20f);
    private static final BigDecimal LOWER_VALUE = BigDecimal.valueOf(1f);

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private InheritanceTax inheritanceTaxMock;

    private FieldErrorResponse businessValidationError;

    private IHTValidationRule underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        businessValidationError = FieldErrorResponse.builder().build();

        this.underTest = new IHTValidationRule(businessValidationMessageService);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
    }

    @Test
    public void testValidateWithSuccess() {
        when(inheritanceTaxMock.getGrossValue()).thenReturn(HIGHER_VALUE);
        when(inheritanceTaxMock.getNetValue()).thenReturn(LOWER_VALUE);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertThat(validationError.isEmpty(), is(true));
    }

    @Test
    public void testValidateWithSuccessWhenIhtIsNull() {
        when(ccdDataMock.getIht()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertThat(validationError.isEmpty(), is(true));
    }

    @Test
    public void testValidateFailureWhenNetHigherThanGross() {
        when(inheritanceTaxMock.getGrossValue()).thenReturn(LOWER_VALUE);
        when(inheritanceTaxMock.getNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, IHT_NET_GREATER_THAN_GROSS))
                .thenReturn(businessValidationError);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertThat(validationError.isEmpty(), is(false));
        verify(businessValidationMessageService, times(1)).generateError(BUSINESS_ERROR, IHT_NET_GREATER_THAN_GROSS);
        assertTrue(validationError.contains(businessValidationError));
    }
}
