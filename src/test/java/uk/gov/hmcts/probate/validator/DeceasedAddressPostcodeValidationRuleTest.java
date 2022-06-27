package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;


class DeceasedAddressPostcodeValidationRuleTest {

    @InjectMocks
    private DeceasedAddressPostcodeValidationRule deceasedAddressPostcodeValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdData;
    @Mock
    private Deceased deceasedMock;
    @Mock
    private SolsAddress addressMock;

    private FieldErrorResponse executorPostcodeIsNullError;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        executorPostcodeIsNullError = FieldErrorResponse.builder().message("deceasedPostcodeIsNull").build();

        when(ccdData.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("deceasedPostcodeIsNull")))
            .thenReturn(executorPostcodeIsNullError);
    }

    @Test
    void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);

        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldReturnPostcodeErrorMessageWhenNoPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");

        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldReturnAddressAndPostCodeErrorMessageWhenNullAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(null);
        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldReturnPostcodeErrorMessageWhenNullPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");
        when(addressMock.getPostCode()).thenReturn(null);

        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldReturnPostcodeErrorMessageWhenEmptyPostcodeProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getAddressLine1()).thenReturn("line1");
        when(addressMock.getPostCode()).thenReturn("");

        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldNotReturnErrorMessagesWhenAddressAndPostcodeProvided() {
        when(deceasedMock.getAddress())
            .thenReturn(SolsAddress.builder().addressLine1("1 White St").postCode("SW1 1AZ").build());

        List<FieldErrorResponse> errors = deceasedAddressPostcodeValidationRule.validate(ccdData);

        assertTrue(errors.isEmpty());
    }

}
