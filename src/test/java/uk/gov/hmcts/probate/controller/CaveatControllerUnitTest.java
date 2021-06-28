package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.CreditAccountPaymentTransformer;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.CaveatorEmailAddressValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsEmailValidationRule;
import uk.gov.hmcts.probate.validator.CaveatsExpiryValidationRule;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.SolicitorPaymentMethodValidationRule;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaveatControllerUnitTest {

    private CaveatController underTest;

    @Mock
    private List<CaveatsEmailValidationRule> validationRuleCaveats;
    @Mock
    private List<CaveatsExpiryValidationRule> validationRuleCaveatsExpiry;
    @Mock
    private CaveatDataTransformer caveatDataTransformer;
    @Mock
    private CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private CaveatNotificationService caveatNotificationService;
    @Mock
    private ConfirmationResponseService confirmationResponseService;
    @Mock
    private PaymentsService paymentsService;
    @Mock
    private FeeService feeService;
    @Mock
    private CreditAccountPaymentTransformer creditAccountPaymentTransformer;
    @Mock
    private CreditAccountPaymentValidationRule creditAccountPaymentValidationRule;

    private static final String AUTH = "Auth";

    @Mock
    private CaveatCallbackRequest caveatCallbackRequest;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private FeeResponse feeResponseMock;
    @Mock
    private CaveatCallbackResponse caveatCallbackResponse;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private CreditAccountPayment creditAccountPaymentMock;
    @Mock
    private SolicitorPaymentMethodValidationRule solicitorPaymentMethodValidationRuleMock;
    @Mock
    private PaymentResponse paymentResponseMock;
    @Mock
    private List<CaveatorEmailAddressValidationRule> allCaveatorEmailAddressValidationRule;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new CaveatController(validationRuleCaveats, validationRuleCaveatsExpiry, caveatDataTransformer,
            caveatCallbackResponseTransformer, eventValidationService, notificationService, caveatNotificationService,
            confirmationResponseService, allCaveatorEmailAddressValidationRule, paymentsService, feeService, creditAccountPaymentTransformer,
            creditAccountPaymentValidationRule, solicitorPaymentMethodValidationRuleMock);

    }

    @Test
    public void shouldValidateWithNoErrors() throws NotificationClientException {
        when(feeService.getCaveatFeesData()).thenReturn(feeResponseMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(creditAccountPaymentTransformer.transform(caveatDetailsMock, feeResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(paymentsService.getCreditAccountPaymentResponse(AUTH, creditAccountPaymentMock))
            .thenReturn(paymentResponseMock);
        when(eventValidationService.validateCaveatPaymentResponse(any(), any(), any()))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.solsCaveatRaise(caveatCallbackRequest, paymentResponseMock))
            .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.validate(AUTH, caveatCallbackRequest,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }

    @Test(expected = BusinessValidationException.class)
    public void shouldValidateWithPaymentMethodErrors() throws NotificationClientException {
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        doThrow(BusinessValidationException.class).when(solicitorPaymentMethodValidationRuleMock)
            .validate(caveatDetailsMock);
        underTest.validate(AUTH, caveatCallbackRequest, bindingResultMock);
    }

    @Test
    public void shouldValidateWithPaymentErrors() throws NotificationClientException {
        when(feeService.getCaveatFeesData()).thenReturn(feeResponseMock);
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetailsMock);
        when(creditAccountPaymentTransformer.transform(caveatDetailsMock, feeResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(paymentsService.getCreditAccountPaymentResponse(AUTH, creditAccountPaymentMock))
            .thenReturn(paymentResponseMock);
        when(eventValidationService.validateCaveatPaymentResponse(any(), any(), any()))
            .thenReturn(caveatCallbackResponse);
        when(caveatCallbackResponse.getErrors()).thenReturn(Arrays.asList("Error"));
        ResponseEntity<CaveatCallbackResponse> response = underTest.validate(AUTH, caveatCallbackRequest,
            bindingResultMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
        assertThat(response.getBody().getErrors().size(), is(1));
    }

    @Test
    public void shouldDefaultSolsPBA() {
        when(caveatCallbackResponseTransformer.transformCaseForSolicitorPBANumbers(caveatCallbackRequest, AUTH))
            .thenReturn(caveatCallbackResponse);
        ResponseEntity<CaveatCallbackResponse> response = underTest.defaulsSolicitorNextStepsForPBANumbers(AUTH,
            caveatCallbackRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(caveatCallbackResponse));
    }
}
