package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseServiceRequestValidationRuleTest {

    private static final Long CASE_ID = 123456789L;
    private static final String USER_MESSAGE = "English message";
    private static final String USER_MESSAGE_WELSH = "Welsh message";

    @Mock
    private PaymentsService paymentsService;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @InjectMocks
    private CaseServiceRequestValidationRule caseServiceRequestValidationRule;

    @Test
    void shouldThrowBusinessValidationExceptionForCaseDetailsWhenServiceRequestExists() {
        CaseDetails caseDetails = new CaseDetails(null, null, CASE_ID);
        when(paymentsService.retrievePayments(String.valueOf(CASE_ID)))
                .thenReturn(PaymentsResponse.builder().payments(List.of()).build());
        when(businessValidationMessageRetriever.getMessage(
                CaseServiceRequestValidationRule.SERVICE_REQUEST_EXISTS, null, Locale.UK))
                .thenReturn(USER_MESSAGE);
        when(businessValidationMessageRetriever.getMessage(
                CaseServiceRequestValidationRule.SERVICE_REQUEST_EXISTS_WELSH, null, Locale.UK))
                .thenReturn(USER_MESSAGE_WELSH);

        BusinessValidationException exception =
                assertThrows(BusinessValidationException.class,
                        () -> caseServiceRequestValidationRule.validate(caseDetails));

        assertAll(
                () -> assertEquals(USER_MESSAGE, exception.getUserMessage()),
                () -> assertEquals("Service request already exists for case: " + CASE_ID, exception.getMessage()),
                () -> assertArrayEquals(new String[]{USER_MESSAGE_WELSH}, exception.getAdditionalMessages())
        );
        verify(paymentsService).retrievePayments(String.valueOf(CASE_ID));
    }

    @Test
    void shouldThrowBusinessValidationExceptionForCaveatDetailsWhenServiceRequestExists() {
        CaveatDetails caveatDetails = new CaveatDetails(null, null, CASE_ID);
        when(paymentsService.retrievePayments(String.valueOf(CASE_ID)))
                .thenReturn(PaymentsResponse.builder().payments(List.of()).build());
        when(businessValidationMessageRetriever.getMessage(
                CaseServiceRequestValidationRule.SERVICE_REQUEST_EXISTS, null, Locale.UK))
                .thenReturn(USER_MESSAGE);
        when(businessValidationMessageRetriever.getMessage(
                CaseServiceRequestValidationRule.SERVICE_REQUEST_EXISTS_WELSH, null, Locale.UK))
                .thenReturn(USER_MESSAGE_WELSH);

        BusinessValidationException exception =
                assertThrows(BusinessValidationException.class,
                        () -> caseServiceRequestValidationRule.validate(caveatDetails));

        assertAll(
                () -> assertEquals(USER_MESSAGE, exception.getUserMessage()),
                () -> assertEquals("Service request already exists for case: " + CASE_ID, exception.getMessage()),
                () -> assertArrayEquals(new String[]{USER_MESSAGE_WELSH}, exception.getAdditionalMessages())
        );
        verify(paymentsService).retrievePayments(String.valueOf(CASE_ID));
    }

    @Test
    void shouldNotThrowExceptionWhenPaymentsResponseIsNull() {
        CaseDetails caseDetails = new CaseDetails(null, null, CASE_ID);
        when(paymentsService.retrievePayments(String.valueOf(CASE_ID))).thenReturn(null);

        assertDoesNotThrow(() -> caseServiceRequestValidationRule.validate(caseDetails));

        verify(paymentsService).retrievePayments(String.valueOf(CASE_ID));
        verifyNoInteractions(businessValidationMessageRetriever);
    }

    @Test
    void shouldNotThrowExceptionWhenPaymentsListIsNull() {
        CaseDetails caseDetails = new CaseDetails(null, null, CASE_ID);
        when(paymentsService.retrievePayments(String.valueOf(CASE_ID)))
                .thenReturn(PaymentsResponse.builder().payments(null).build());

        assertDoesNotThrow(() -> caseServiceRequestValidationRule.validate(caseDetails));

        verify(paymentsService).retrievePayments(String.valueOf(CASE_ID));
        verifyNoInteractions(businessValidationMessageRetriever);
    }
}

