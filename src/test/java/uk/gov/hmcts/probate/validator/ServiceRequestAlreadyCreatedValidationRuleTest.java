package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ServiceRequestAlreadyCreatedValidationRuleTest {
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private ServiceRequestAlreadyCreatedValidationRule validationRule;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private CaseDetails caseDetails;

    private CaveatDetails caveatDetails;
    @Mock
    private CaveatData caveatDataMock;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        validationRule = new ServiceRequestAlreadyCreatedValidationRule(businessValidationMessageRetriever);
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .serviceRequestReference("2020-1599477846961")
                .payments(Arrays.asList(
                        new CollectionMember<Payment>("id",
                                Payment.builder()
                                        .reference("Reference-123")
                                        .status("Success")
                                        .build())))
                .build(),
                LAST_MODIFIED, CASE_ID);
        caveatDetails = new CaveatDetails(CaveatData.builder()
                .serviceRequestReference("2020-1599477846961")
                .payments(Arrays.asList(
                        new CollectionMember<Payment>("id",
                                Payment.builder()
                                        .reference("Reference-123")
                                        .status("Success")
                                        .build())))
                .build(),
                LAST_MODIFIED, CASE_ID);
        Mockito.when(
                businessValidationMessageRetriever.getMessage("code", null, Locale.UK)
        ).thenReturn("message");
    }

    private static Stream<Arguments> data() {
        return Stream.of(arguments("2020-1599477846961", null),
                arguments(null, "Success")
        );
    }

    @Test
    void shouldThrowExceptionForPaymentSuccess() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            validationRule.validate(caseDetails);
        });

        assertEquals("Service request for payment already created for case:12345678987654321",
                exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldNotThrowExceptionForNoRefAndNoStatus(final String reference, final String status) {
        CaseDetails caseDetails1 = new CaseDetails(CaseData.builder()
                .serviceRequestReference(reference)
                .payments(Arrays.asList(
                        new CollectionMember<Payment>("id",
                                Payment.builder()
                                        .reference("Reference-123")
                                        .status(status)
                                        .build())))
                .build(),
                LAST_MODIFIED, CASE_ID);

        validationRule.validate(caseDetails1);

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionForPaymentSuccessCaveat() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            validationRule.validate(caveatDetails);
        });

        assertEquals("Service request for payment already created for case:12345678987654321",
                exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void shouldNotThrowExceptionForNoRefAndNoStatusCaveat(final String reference, final String status) {
        CaveatDetails caseDetails1 = new CaveatDetails(CaveatData.builder()
                .serviceRequestReference(reference)
                .payments(Arrays.asList(
                        new CollectionMember<Payment>("id",
                                Payment.builder()
                                        .reference("Reference-123")
                                        .status(status)
                                        .build())))
                .build(),
                LAST_MODIFIED, CASE_ID);

        validationRule.validate(caseDetails1);

        verify(businessValidationMessageRetriever, never()).getMessage(any(), any(), any());
    }

}
