package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.BusinessValidationError;
import uk.gov.hmcts.probate.model.CCDData;
import uk.gov.hmcts.probate.model.InheritanceTax;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_NET_GREATER_THAN_GROSS;
import static uk.gov.hmcts.probate.validator.ValidationRule.BUSINESS_ERROR;

@RunWith(MockitoJUnitRunner.class)
public class IHTValidationRuleTest {

    private static final float HIGHER_VALUE = 20f;
    private static final float LOWER_VALUE = 1f;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;
    @Mock
    private BusinessValidationError businessValidationError;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private InheritanceTax inheritanceTaxMock;

    private IHTValidationRule underTest;

    @Before
    public void setUp() throws Exception {
        this.underTest = new IHTValidationRule(businessValidationMessageService);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
    }

    @Test
    public void testValidateWithSuccess() throws Exception {
        when(inheritanceTaxMock.getGrossValue()).thenReturn(HIGHER_VALUE);
        when(inheritanceTaxMock.getNetValue()).thenReturn(LOWER_VALUE);

        List<BusinessValidationError> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertThat(validationError.isEmpty(), is(true));
    }

    @Test
    public void testValidateWithSuccessWhenIhtIsNull() throws Exception {
        when(ccdDataMock.getIht()).thenReturn(null);
        when(inheritanceTaxMock.getGrossValue()).thenReturn(HIGHER_VALUE);
        when(inheritanceTaxMock.getNetValue()).thenReturn(LOWER_VALUE);

        List<BusinessValidationError> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertThat(validationError.isEmpty(), is(true));
    }

    @Test
    public void testValidateFailureWhenNetHigherThanGross() throws Exception {
        when(inheritanceTaxMock.getGrossValue()).thenReturn(LOWER_VALUE);
        when(inheritanceTaxMock.getNetValue()).thenReturn(HIGHER_VALUE);
        when(
            businessValidationMessageService.generateError(BUSINESS_ERROR, IHT_NET_GREATER_THAN_GROSS)
        ).thenReturn(businessValidationError);

        List<BusinessValidationError> validationError = underTest.validate(ccdDataMock);

        assertThat(validationError.isEmpty(), is(false));
        verify(businessValidationMessageService, times(1)).generateError(BUSINESS_ERROR, IHT_NET_GREATER_THAN_GROSS);
        assertTrue(validationError.contains(businessValidationError));
    }
}
