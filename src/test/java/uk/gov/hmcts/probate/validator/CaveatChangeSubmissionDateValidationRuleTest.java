package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

class CaveatChangeSubmissionDateValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Mock
    private PaymentsService paymentsService;
    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private CaveatChangeSubmissionDateValidationRule underTest;
    private FieldErrorResponse fieldErrorResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CaveatChangeSubmissionDateValidationRule(paymentsService, businessValidationMessageService);

        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE,
                "future-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD,
                "before-dod-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID,
                "dod-missing-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_MISSING,
                "submitted-missing-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_MISSING_PAYMENT,
                "payment-missing-en");
    }

    @Test
    void shouldReturnNoErrorsWhenSubmissionDateValid() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 2, 1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .build();
        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(0, errors.size());
    }

    @Test
    void shouldReturnErrorsWhenSubmissionDateIsInFuture() {
        LocalDate today = LocalDate.now();
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(today.plusDays(1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("future-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldReturnErrorsWhenSubmissionDateBeforeDod() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 2))
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("before-dod-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldReturnErrorsWhenDodMissing() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(null)
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("dod-missing-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldReturnErrorsWhenDodInFuture() {
        LocalDate today = LocalDate.now();
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(today.plusDays(1))
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("dod-missing-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldReturnErrorsWhenSubmissionDateMissing() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(null)
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(true);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("submitted-missing-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldReturnErrorWhenPaymentMissingAndPaymentTakenIsNotYes() {
        LocalDate today = LocalDate.now();
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(today.minusDays(1))
                .deceasedDateOfDeath(today.minusDays(2))
                .paymentTaken("No")
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(false);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("payment-missing-en"), errors.stream().map(FieldErrorResponse::getMessage).toList());
    }

    @Test
    void shouldNotReturnErrorWhenPaymentTakenIsYesAndNoPaymentRecordFound() {
        LocalDate today = LocalDate.now();
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(today.minusDays(1))
                .deceasedDateOfDeath(today.minusDays(2))
                .paymentTaken("Yes")
                .build();

        when(paymentsService.isPaymentSuccessByCaseId("1")).thenReturn(false);
        List<FieldErrorResponse> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(0, errors.size());
    }

    private void stubMessage(String code, String message) {
        fieldErrorResponse = FieldErrorResponse.builder()
                .message(message)
                .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR,code)).thenReturn(fieldErrorResponse);
    }
}

