package uk.gov.hmcts.probate.validator;

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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


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
    private FieldErrorResponse executorPostcodeIsNullError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        executorAddressIsNullError = FieldErrorResponse.builder().message("deceasedAddressIsNull").build();
        executorPostcodeIsNullError = FieldErrorResponse.builder().message("deceasedPostcodeIsNull").build();

        when(ccdData.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(eq(ValidationRule.BUSINESS_ERROR), eq("deceasedAddressIsNull")))
                .thenReturn(executorAddressIsNullError);

        when(businessValidationMessageService.generateError(eq(ValidationRule.BUSINESS_ERROR), eq("deceasedPostcodeIsNull")))
                .thenReturn(executorPostcodeIsNullError);
    }

    @Test
    public void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
        assertTrue(errors.contains(executorPostcodeIsNullError));
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
    public void shouldReturnPostcodeErrorMessageWhenNoPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    public void shouldReturnAddressAndPostCodeErrorMessageWhenNullAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(null);
        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertThat(errors, containsInAnyOrder(executorAddressIsNullError, executorPostcodeIsNullError));
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
    public void shouldReturnPostcodeErrorMessageWhenNullPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");
        when(addressMock.getPostCode()).thenReturn(null);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    public void shouldReturnPostcodeErrorMessageWhenEmptyPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");
        when(addressMock.getPostCode()).thenReturn("");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }
}
