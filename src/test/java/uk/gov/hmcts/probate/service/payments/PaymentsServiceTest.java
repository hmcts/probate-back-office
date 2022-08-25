package uk.gov.hmcts.probate.service.payments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PaymentsServiceTest {
    @MockBean(name = "restTemplate")
    private RestTemplate restTemplate;
    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private PaymentsService paymentsService;

    @MockBean
    private CreditAccountPayment creditAccountPayment;

    @MockBean
    private ServiceRequestClient serviceRequestClient;

    @MockBean
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Mock
    private PaymentResponse paymentResponse;

    @Mock
    private HttpClientErrorException httpClientErrorExceptionMock;

    @Mock
    private SecurityUtils securityUtilsMock;
    @Mock
    private ServiceRequestClient serviceRequestClientMock;
    @Mock
    private CasePaymentBuilder casePaymentBuilderMock;


    private static final String AUTH_TOKEN = "Bearer .AUTH";

    @Test
    void shouldGetPaymentResponse() {
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
            any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.of(Optional.of(paymentResponse)));

        PaymentResponse returnedPaymentResponse = paymentsService.getCreditAccountPaymentResponse(AUTH_TOKEN,
            creditAccountPayment);

        assertEquals(paymentResponse, returnedPaymentResponse);
    }

    @Test
    void shouldGetExceptionOnNullPaymentResponse() {
        assertThrows(NullPointerException.class, () -> {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                    any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.of(Optional.empty()));

            paymentsService.getCreditAccountPaymentResponse(AUTH_TOKEN, creditAccountPayment);
        });
    }

    @Test
    void shouldFailOnAccountDeletedWith403() {
        try {
            String body = "{\"reference\":\"RC-1599-4778-4711-5958\",\"date_created\":\"2020-09-07T11:24:07.160+0000\","
                + "\"status\":\"failed\",\"payment_group_reference\":\"2020-1599477846961\","
                + "\"status_histories\":[{\"status\":\"failed\",\"error_code\":\"CA-E0004\",\"error_message\":\"Your "
                + "account is deleted\",\"date_created\":\"2020-09-07T11:24:07.169+0000\","
                + "\"date_updated\":\"2020-09-07T11:24:07.169+0000\"}]}";
            when(httpClientErrorExceptionMock.getResponseBodyAsString()).thenReturn(body);
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
            when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn("Failed with some error");

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);
        } catch (BusinessValidationException e) {
            assertEquals("Failed with some error", e.getUserMessage());
        }
    }

    @Test
    void shouldFailWith404() {
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(NOT_FOUND);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Account information could not be found", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWith422() {
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(UNPROCESSABLE_ENTITY);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Invalid or missing attribute", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWith400() {
        try {
            when(httpClientErrorExceptionMock.getMessage()).thenReturn("other 400 failure");
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(BAD_REQUEST);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Payment Failed", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWith400WithNullMessage() {
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(BAD_REQUEST);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Payment Failed", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWith400DuplicatePayment() {
        try {
            when(httpClientErrorExceptionMock.getMessage()).thenReturn("duplicate payment");
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(BAD_REQUEST);
            when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn("Duplicate payment error");

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Duplicate payment error", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(2)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWith5XX() {
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(INTERNAL_SERVER_ERROR);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Unable to retrieve account information, please try again later", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldFailWithOtherError() {
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(UNAUTHORIZED);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals("Unexpected Exception", e.getUserMessage());
            verify(businessValidationMessageRetriever, times(0)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldGetNewEmailWhenPaymentError() {
        String userMessage = "USER MESSAGE";
        String additionalMessage = "ADDITIONAL MESSAGE";
        try {
            when(restTemplate.exchange(any(URI.class), any(HttpMethod.class),
                any(HttpEntity.class), any(Class.class))).thenThrow(httpClientErrorExceptionMock);
            when(httpClientErrorExceptionMock.getStatusCode()).thenReturn(BAD_REQUEST);
            when(httpClientErrorExceptionMock.getMessage()).thenReturn("duplicate payment");
            String[] empty = {};

            when(businessValidationMessageRetriever.getMessage(
                "creditAccountPaymentErrorMessageDuplicatePayment", empty, UK)).thenReturn(userMessage);
            when(businessValidationMessageRetriever.getMessage(
                "creditAccountPaymentErrorMessageDuplicatePayment2", empty, UK)).thenReturn(additionalMessage);

            paymentsService.getCreditAccountPaymentResponse("Bearer .123", creditAccountPayment);

        } catch (BusinessValidationException e) {
            assertEquals(userMessage, e.getUserMessage());
            assertEquals(Optional.of(additionalMessage),Arrays.stream(e.getAdditionalMessages()).findAny());
            verify(businessValidationMessageRetriever, times(2)).getMessage(any(), any(), any());
        }
    }

    @Test
    void shouldCreateServiceRequest() {
        ServiceRequestDto serviceDto = ServiceRequestDto.builder().build();
        when(securityUtilsMock.getSecurityDTO()).thenReturn(SecurityDTO.builder().build());
        when(serviceRequestClient.createServiceRequest(any(),any(), any()))
                .thenReturn("{\"service_request_reference\":\"abcdef123456\"}");
        String request = paymentsService.createServiceRequest(serviceDto);

        assertEquals("abcdef123456", request);
    }
}
