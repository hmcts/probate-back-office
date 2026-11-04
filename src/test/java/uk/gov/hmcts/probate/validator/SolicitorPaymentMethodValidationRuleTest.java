package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitorPaymentMethodValidationRuleTest {

    private static final String ERROR_MESSAGE =
            "The solicitor payment method selected is not 'fee account'";

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private SolicitorPaymentMethodValidationRule underTest;

    @BeforeEach
    void setUp() {
        underTest = new SolicitorPaymentMethodValidationRule(businessValidationMessageRetriever);
    }

    @Test
    void shouldThrowExceptionForChequePaymentMethodChosen() {
        CaseDetails caseDetails = createCaseDetails("cheque");

        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn(ERROR_MESSAGE);

        assertThrows(BusinessValidationException.class, () -> underTest.validate(caseDetails));
    }

    @Test
    void shouldNotThrowExceptionForFeeAccountPaymentMethodChosen() {
        CaseDetails caseDetails = createCaseDetails("fee account");

        assertDoesNotThrow(() -> underTest.validate(caseDetails));

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionForChequePaymentMethodChosenCaveat() {
        CaveatDetails caveatDetails = createCaveatDetails(ApplicationType.SOLICITOR, "cheque");

        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn(ERROR_MESSAGE);

        assertThrows(BusinessValidationException.class, () -> underTest.validate(caveatDetails));
    }

    @Test
    void shouldNotThrowExceptionForFeeAccountPaymentMethodChosenCaveat() {
        CaveatDetails caveatDetails = createCaveatDetails(ApplicationType.SOLICITOR, "fee account");

        assertDoesNotThrow(() -> underTest.validate(caveatDetails));

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    @Test
    void shouldNotThrowExceptionForNotSolicitor() {
        CaveatDetails caveatDetails = createCaveatDetails(ApplicationType.PERSONAL, "fee account");

        assertDoesNotThrow(() -> underTest.validate(caveatDetails));

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    private CaseDetails createCaseDetails(
            String paymentMethod) {

        CaseData caseData = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsPaymentMethods(paymentMethod)
                .build();

        return new CaseDetails(caseData, null, 1L);
    }

    private CaveatDetails createCaveatDetails(
            ApplicationType applicationType, String paymentMethod) {

        CaveatData caveatData = CaveatData.builder()
                .applicationType(applicationType)
                .solsPaymentMethods(paymentMethod)
                .build();

        return new CaveatDetails(caveatData, null, 1L);
    }
}
