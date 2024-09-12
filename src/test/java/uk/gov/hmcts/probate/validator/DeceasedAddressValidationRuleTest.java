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


class DeceasedAddressValidationRuleTest {

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
    private FieldErrorResponse executorAddressIsNullError2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        executorAddressIsNullError = FieldErrorResponse.builder().message("deceasedAddressIsNull").build();
        executorAddressIsNullError2 = FieldErrorResponse.builder().message("deceasedAddressIsNullWelsh").build();

        when(ccdData.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("deceasedAddressIsNull")))
                .thenReturn(executorAddressIsNullError);
        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("deceasedAddressIsNullWelsh")))
                .thenReturn(executorAddressIsNullError2);

    }

    @Test
    void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    void shouldReturnAddressErrorMessageWhenNoAddressLineProvided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError2));
    }

    @Test
    void shouldReturnAddressAndPostCodeErrorMessageWhenNullAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(null);
        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    void shouldReturnAddressErrorMessageWhenEmptyAddressLine1Provided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");
        when(addressMock.getAddressLine1()).thenReturn("");

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    void shouldReturnAddressErrorMessageWhenNullAddressLine1Provided() {
        when(deceasedMock.getAddress()).thenReturn(addressMock);
        when(addressMock.getPostCode()).thenReturn("PC");
        when(addressMock.getAddressLine1()).thenReturn(null);

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    void shouldNotReturnErrorMessagesWhenAddressProvided() {
        when(deceasedMock.getAddress()).thenReturn(SolsAddress.builder().addressLine1("1 White St").build());

        List<FieldErrorResponse> errors = deceasedAddressValidationRule.validate(ccdData);

        assertTrue(errors.isEmpty());
    }

}
