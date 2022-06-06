package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOB_IN_FUTURE;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOD_BEFORE_DOB;
import static uk.gov.hmcts.probate.validator.DobDodValidationRule.CODE_DOD_IN_FUTURE;

@ExtendWith(SpringExtension.class)
class DobDodValidationRuleTest {

    private static final LocalDate DATE_31_DEC_1970 = LocalDate.of(1970, 12, 31);
    private static final LocalDate DATE_30_DEC_1970 = LocalDate.of(1970, 12, 30);
    private static final LocalDate DATE_01_JAN_1971 = LocalDate.of(1971, 1, 1);
    private static final LocalDate DATE_01_JAN_2099 = LocalDate.of(2099, 1, 1);
    private static final LocalDate DATE_02_JAN_2099 = LocalDate.of(2099, 1, 2);

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private Deceased deceasedMock;

    private DobDodValidationRule underTest;

    @BeforeEach
    public void setUp() {
        underTest = new DobDodValidationRule(businessValidationMessageService);
        when(ccdDataMock.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOD_BEFORE_DOB))
                .thenReturn(FieldErrorResponse.builder().code(CODE_DOD_BEFORE_DOB).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOB_IN_FUTURE))
                .thenReturn(FieldErrorResponse.builder().code(CODE_DOB_IN_FUTURE).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_DOD_IN_FUTURE))
                .thenReturn(FieldErrorResponse.builder().code(CODE_DOD_IN_FUTURE).build());
    }

    @Test
    void testValidateWithSuccessWhenDeceasedIsNull() {
        when(ccdDataMock.getDeceased()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldValidateSuccessWithDobBeforeDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(DATE_31_DEC_1970);
        when(deceasedMock.getDateOfDeath()).thenReturn(DATE_01_JAN_1971);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureWithDobAfterDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(DATE_31_DEC_1970);
        when(deceasedMock.getDateOfDeath()).thenReturn(DATE_30_DEC_1970);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(CODE_DOD_BEFORE_DOB, validationErrors.get(0).getCode());
    }

    @Test
    void shouldValidateFailureWithDobEqualsDod() {
        when(deceasedMock.getDateOfBirth()).thenReturn(DATE_31_DEC_1970);
        when(deceasedMock.getDateOfDeath()).thenReturn(DATE_31_DEC_1970);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(0, validationErrors.size());
    }

    @Test
    void shouldValidateFailureWithDobInTheFuture() {
        when(deceasedMock.getDateOfBirth()).thenReturn(DATE_01_JAN_2099);
        when(deceasedMock.getDateOfDeath()).thenReturn(DATE_02_JAN_2099);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(CODE_DOB_IN_FUTURE, validationErrors.get(0).getCode());
    }

    @Test
    void shouldValidateFailureWithDodInTheFuture() {
        when(deceasedMock.getDateOfBirth()).thenReturn(DATE_01_JAN_1971);
        when(deceasedMock.getDateOfDeath()).thenReturn(DATE_02_JAN_2099);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(CODE_DOD_IN_FUTURE, validationErrors.get(0).getCode());
    }
}
