package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOB_IN_FUTURE;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOD_BEFORE_DOB;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOD_IN_FUTURE;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOD_ON_DOB;

@RunWith(MockitoJUnitRunner.class)
public class DobDodValidationRuleTest {

    private static final String DATE_31_DEC_1970 = "1970-12-31";
    private static final String DATE_30_DEC_1970 = "1970-12-30";
    private static final String DATE_01_JAN_1971 = "1971-01-01";
    private static final String DATE_01_JAN_2099 = "2099-01-01";
    private static final String DATE_02_JAN_2099 = "2099-01-02";

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private Deceased deceasedMock;

    private FieldErrorResponse businessValidationError;

    private DobDodValidationRule underTest;

    @Before
    public void setUp() {
        underTest = new DobDodValidationRule(businessValidationMessageService);
        businessValidationError = FieldErrorResponse.builder().build();
        when(ccdDataMock.getDeceased()).thenReturn(deceasedMock);
    }


    @Test
    public void testValidateWithSuccessWhenDeceasedIsNull() {
        when(ccdDataMock.getDeceased()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertThat(validationError.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateSuccessWithDobBeforeDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(getDate(DATE_31_DEC_1970));
        when(deceasedMock.getDateOfDeath()).thenReturn(getDate(DATE_01_JAN_1971));

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertThat(validationErrors.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateFailureWithDobAfterDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(getDate(DATE_31_DEC_1970));
        when(deceasedMock.getDateOfDeath()).thenReturn(getDate(DATE_30_DEC_1970));
        when(
                businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOD_BEFORE_DOB)
        ).thenReturn(businessValidationError);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.contains(businessValidationError));
    }

    @Test
    public void shouldValidateFailureWithDobEqualsDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(getDate(DATE_31_DEC_1970));
        when(deceasedMock.getDateOfDeath()).thenReturn(getDate(DATE_31_DEC_1970));
        when(
                businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOD_ON_DOB)
        ).thenReturn(businessValidationError);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);
        assertTrue(validationError.contains(businessValidationError));

        //validationError.ifPresent(error -> assertThat(error, is(businessValidationError)));
    }

    @Test
    public void shouldValidateFailureWithDobInTheFuture() {
        when(deceasedMock.getDateOfBirth()).thenReturn(getDate(DATE_01_JAN_2099));
        when(deceasedMock.getDateOfDeath()).thenReturn(getDate(DATE_02_JAN_2099));
        when(
                businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOB_IN_FUTURE)
        ).thenReturn(businessValidationError);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);
    }

    @Test
    public void shouldValidateFailureWithDodInTheFuture() {
        when(deceasedMock.getDateOfBirth()).thenReturn(getDate(DATE_01_JAN_1971));
        when(deceasedMock.getDateOfDeath()).thenReturn(getDate(DATE_02_JAN_2099));
        when(
                businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOD_IN_FUTURE)
        ).thenReturn(businessValidationError);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);
        assertTrue(validationError.contains(businessValidationError));
    }

    private LocalDate getDate(String dateString) {
        return LocalDate.parse(dateString);
    }

}
