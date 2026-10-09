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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.IntestacyDivorceOrSeparationDateValidationRule.INVALID_DIVORCE_OR_SEPARATION_DATE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;

@ExtendWith(SpringExtension.class)
class IntestacyDivorceOrSeparationDateValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private Deceased deceasedMock;

    private IntestacyDivorceOrSeparationDateValidationRule underTest;

    @BeforeEach
    public void setUp() {
        underTest = new IntestacyDivorceOrSeparationDateValidationRule(businessValidationMessageService);
        when(ccdDataMock.getDeceased()).thenReturn(deceasedMock);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, INVALID_DIVORCE_OR_SEPARATION_DATE))
                .thenReturn(FieldErrorResponse.builder().code(INVALID_DIVORCE_OR_SEPARATION_DATE).build());
    }

    @Test
    void testValidateWithSuccessWhenDeceasedIsNull() {
        when(ccdDataMock.getDeceased()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void returnsNoErrorsWhenDivorceDateIsExactlyAfterDateOfBirthAndBeforeDeathAndApplicationDate() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(deceasedMock.getDateOfDivorcedCPJudicially()).thenReturn("1980-01-02");
        when(deceasedMock.getDateOfBirth()).thenReturn(LocalDate.of(1980, 1, 1));
        when(deceasedMock.getDateOfDeath()).thenReturn(LocalDate.of(2021, 1, 1));
        when(ccdDataMock.getApplicationSubmissionDate()).thenReturn("2022-01-01");

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldReturnsErrorWhenMaritalStatusIsJudiciallySeparatedAndDivorceDateInvalid() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(deceasedMock.getDateOfDivorcedCPJudicially()).thenReturn("2025-01-01");
        when(deceasedMock.getDateOfBirth()).thenReturn(LocalDate.of(1980, 1, 1));
        when(deceasedMock.getDateOfDeath()).thenReturn(LocalDate.of(2021, 1, 1));
        when(ccdDataMock.getApplicationSubmissionDate()).thenReturn("2024-01-01");

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertEquals(INVALID_DIVORCE_OR_SEPARATION_DATE, validationErrors.getFirst().getCode());
    }

    @Test
    void returnsNoErrorsWhenMaritalStatusIsNull() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(null);
        when(deceasedMock.getDateOfDivorcedCPJudicially()).thenReturn("2000-01-01");
        when(deceasedMock.getDateOfBirth()).thenReturn(LocalDate.of(1980, 1, 1));
        when(deceasedMock.getDateOfDeath()).thenReturn(LocalDate.of(2021, 1, 1));
        when(ccdDataMock.getApplicationSubmissionDate()).thenReturn("2022-01-01");

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertTrue(validationErrors.isEmpty());
    }
}
