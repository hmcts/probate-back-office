package uk.gov.hmcts.probate.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;


public class DeceasedAddressValidationRuleTest {

    @InjectMocks
    private DeceasedAddressValidationRule deceasedAddressValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdData;
    @Mock
    private Deceased deceasedMock;
    @Mock
    private SolsAddress addressMock;

    private FieldErrorResponse executorAddressIsNullError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        executorAddressIsNullError = FieldErrorResponse.builder().message("deceasedAddressIsNull").build();

        when(ccdData.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("deceasedAddressIsNull")))
                .thenReturn(executorAddressIsNullError);

    }

    @Test
    public void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldReturnAddressErrorMessageWhenNoAddressLineProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldReturnAddressAndPostCodeErrorMessageWhenNullAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(null);
        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldReturnAddressErrorMessageWhenEmptyAddressLine1Provided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");
        when(addressMock.getAddressLine1()).thenReturn("");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldReturnAddressErrorMessageWhenNullAddressLine1Provided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");
        when(addressMock.getAddressLine1()).thenReturn(null);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldNotReturnErrorMessagesWhenAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(SolsAddress.builder().addressLine1("1 White St").build());

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        Assert.assertTrue(errors.isEmpty());
    }

}
