package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.service.payments.CreditAccountPaymentTransformer;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NextStepsUnitTest {

    private NextStepsController underTest;

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CCDDataTransformer ccdBeanTransformerMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private FeeService feeServiceMock;

    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private InheritanceTax inheritanceTaxMock;
    @Mock
    private Fee feeMock;
    @Mock
    private FeesResponse feesResponseMock;
    @Mock
    private CallbackResponse callbackResponseMock;
    @Mock
    private StateChangeService stateChangeServiceMock;
    @Mock
    private PaymentsService paymentsService;
    @Mock
    private CreditAccountPaymentTransformer creditAccountPaymentTransformer;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private CreditAccountPaymentValidationRule creditAccountPaymentValidationRule;
    
    @Mock
    private CreditAccountPayment creditAccountPaymentMock;
    @Mock
    private PaymentResponse paymentResponseMock;
    
    private static final String AUTH = "Auth";

    @MockBean
    private AppInsights appInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new NextStepsController(eventValidationService, ccdBeanTransformerMock, 
            confirmationResponseServiceMock, callbackResponseTransformerMock, objectMapperMock, feeServiceMock, 
            stateChangeServiceMock, paymentsService, creditAccountPaymentTransformer, 
            creditAccountPaymentValidationRule);

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(creditAccountPaymentTransformer.transform(caseDetailsMock, feesResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(callbackResponseTransformerMock
            .transformForSolicitorComplete(callbackRequestMock, feesResponseMock)).thenReturn(callbackResponseMock);
        
        when(feeServiceMock.getAllFeesData(null, 0L, 0L)).thenReturn(feesResponseMock);
        when(paymentsService.getCreditAccountPaymentResponse(AUTH, creditAccountPaymentMock))
            .thenReturn(paymentResponseMock);
    }

    @Test
    public void shouldValidateWithNoErrors() {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(creditAccountPaymentTransformer.transform(caseDetailsMock, feesResponseMock))
            .thenReturn(creditAccountPaymentMock);
        when(callbackResponseTransformerMock
            .transformForSolicitorComplete(callbackRequestMock, feesResponseMock))
            .thenReturn(callbackResponseMock);
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Collections.emptyList());
        when(eventValidationService.validatePaymentResponse(caseDetailsMock, paymentResponseMock, 
            creditAccountPaymentValidationRule)).thenReturn(creditPaymentResponseError);

        ResponseEntity<CallbackResponse> response = underTest.validate(AUTH, callbackRequestMock,
            bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test
    public void shouldValidateWithPaymentError() {
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(creditAccountPaymentTransformer.transform(caseDetailsMock, feesResponseMock))
            .thenReturn(creditAccountPaymentMock);
        CallbackResponse creditPaymentResponseError = Mockito.mock(CallbackResponse.class);
        when(creditPaymentResponseError.getErrors()).thenReturn(Arrays.asList("error"));
        when(eventValidationService.validatePaymentResponse(caseDetailsMock, paymentResponseMock, 
            creditAccountPaymentValidationRule)).thenReturn(creditPaymentResponseError);

        ResponseEntity responseEntity = underTest.validate(AUTH, callbackRequestMock,
            bindingResultMock, httpServletRequestMock);
        assertThat(responseEntity.getBody(), is(creditPaymentResponseError));
    }

    @Test(expected = BadRequestException.class)
    public void shouldValidateWithError() {
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());

        underTest.validate(AUTH, callbackRequestMock,
            bindingResultMock, httpServletRequestMock);
    }

    @Test(expected = BadRequestException.class)
    public void shouldValidateWithErrorAndLogRequest() throws JsonProcessingException {
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(Optional.empty());
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenThrow(JsonProcessingException.class);

        underTest.validate(AUTH, callbackRequestMock,
            bindingResultMock, httpServletRequestMock);
    }

    @Test
    public void shouldValidateWithNoErrorsForStateChange() {
        Optional<String> newState = Optional.of("changedState");
        when(stateChangeServiceMock.getChangedStateForCaseReview(caseDataMock)).thenReturn(newState);
        when(callbackResponseTransformerMock.transformWithConditionalStateChange(callbackRequestMock, newState))
            .thenReturn(callbackResponseMock);


        ResponseEntity<CallbackResponse> response = underTest.validate(AUTH, callbackRequestMock,
            bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

}
