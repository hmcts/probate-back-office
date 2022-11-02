package uk.gov.hmcts.probate.service.payments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestPaymentResponseDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.idam.TokenRequest;
import uk.gov.hmcts.reform.probate.model.idam.TokenResponse;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.WILL_LODGEMENT;

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
    @MockBean
    private CcdClientApi ccdClientApi;
    @MockBean
    private IdamApi idamApi;

    private static final String USER_TOKEN = "1312jdhdh";
    private static final String CASEWORKER_PASSWORD = "caseworkerPassword";
    private static final String CASEWORKER_USER_NAME = "caseworkerUserName";
    private static final String AUTH_CLIENT_SECRET = "authClientSecret";
    private static final String AUTH_CLIENT_ID = "authClientId";
    private static final String REDIRECT = "http://redirect";
    private static final String BEARER = "Bearer ";
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

    private static Stream<Arguments> paymentMethodAndStatus() {
        return Stream.of(arguments("Payment by account", "Paid"),
                arguments("Payment by account", "Not Paid"),
                arguments("Payment by account", "Partially paid"),
                arguments("card", "Paid"),
                arguments("card", "Not Paid"),
                arguments("card", "Partially paid"));
    }

    @ParameterizedTest
    @MethodSource("paymentMethodAndStatus")
    void shouldUpdateCaseFromServiceRequestForGop(final String paymentMethod, final String paymentStatus) {
        final ServiceRequestUpdateResponseDto responseDto = ServiceRequestUpdateResponseDto.builder()
                .serviceRequestReference("2020-1599477846961")
                .ccdCaseNumber("1661448513999408")
                .serviceRequesAmount(BigDecimal.valueOf(50.00))
                .serviceRequestStatus(paymentStatus)
                .serviceRequestPaymentResponseDto(ServiceRequestPaymentResponseDto.builder()
                        .paymentAmount(BigDecimal.valueOf(50.00))
                        .paymentReference("RC-1234")
                        .paymentMethod(paymentMethod)
                        .caseReference("example of case ref")
                        .accountNumber("PBA123")
                        .build())
                .build();
        when(securityUtilsMock.getSecurityDTO()).thenReturn(SecurityDTO.builder().build());
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(new TokenResponse(USER_TOKEN, "360000", USER_TOKEN, null, null, null));
        when(securityUtilsMock.getCaseworkerToken()).thenReturn("AUTH");
        when(securityUtilsMock.generateServiceToken()).thenReturn("S2S");
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);
        when(idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        paymentsService.updateCaseFromServiceRequest(responseDto, GRANT_OF_REPRESENTATION);

        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(),
                        any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("paymentMethodAndStatus")
    void shouldUpdateCaseFromServiceRequestForCaveat(final String paymentMethod, final String paymentStatus) {
        final ServiceRequestUpdateResponseDto responseDto = ServiceRequestUpdateResponseDto.builder()
                .serviceRequestReference("2020-1599477846961")
                .ccdCaseNumber("1661448513999408")
                .serviceRequesAmount(BigDecimal.valueOf(50.00))
                .serviceRequestStatus(paymentStatus)
                .serviceRequestPaymentResponseDto(ServiceRequestPaymentResponseDto.builder()
                        .paymentAmount(BigDecimal.valueOf(50.00))
                        .paymentReference("RC-1234")
                        .paymentMethod(paymentMethod)
                        .caseReference("example of case ref")
                        .accountNumber("PBA123")
                        .build())
                .build();
        when(securityUtilsMock.getSecurityDTO()).thenReturn(SecurityDTO.builder().build());
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(new TokenResponse(USER_TOKEN, "360000", USER_TOKEN, null, null,
                        null));
        when(securityUtilsMock.getCaseworkerToken()).thenReturn("AUTH");
        when(securityUtilsMock.generateServiceToken()).thenReturn("S2S");
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);
        when(idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        paymentsService.updateCaseFromServiceRequest(responseDto, CAVEAT);

        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(),
                        any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("serviceRequestStatusAndPaymentMethodAndExceptionMessage")
    void shouldThrowExceptionForUpdateCaseFromServiceRequestInvalidCaseType(String status,
                                                                        String paymentMethod, String message) {
        final ServiceRequestUpdateResponseDto responseDto = ServiceRequestUpdateResponseDto.builder()
                .serviceRequestReference("2020-1599477846961")
                .ccdCaseNumber("1661448513999408")
                .serviceRequesAmount(BigDecimal.valueOf(50.00))
                .serviceRequestStatus(status)
                .serviceRequestPaymentResponseDto(ServiceRequestPaymentResponseDto.builder()
                        .paymentAmount(BigDecimal.valueOf(50.00))
                        .paymentReference("RC-1234")
                        .paymentMethod(paymentMethod)
                        .caseReference("example of case ref")
                        .accountNumber("PBA123")
                        .build())
                .build();
        when(securityUtilsMock.getSecurityDTO()).thenReturn(SecurityDTO.builder().build());
        when(idamApi.generateOpenIdToken(any(TokenRequest.class)))
                .thenReturn(new TokenResponse(USER_TOKEN, "360000", USER_TOKEN, null, null, null));
        when(securityUtilsMock.getCaseworkerToken()).thenReturn("AUTH");
        when(securityUtilsMock.generateServiceToken()).thenReturn("S2S");
        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("id", "Value");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(stringObjectMap, HttpStatus.CONTINUE);
        when(idamApi.getUserDetails(anyString())).thenReturn(responseEntity);
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails =
                Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
        Map caseData = Mockito.mock(Map.class);
        when(caseDetails.getData()).thenReturn(caseData);
        when(ccdClientApi.readForCaseWorker(any(), any(), any())).thenReturn(caseDetails);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentsService.updateCaseFromServiceRequest(responseDto, WILL_LODGEMENT));

        assertEquals(message, exception.getMessage());
        verify(ccdClientApi, times(0))
                .updateCaseAsCaseworker(any(), any(), any(),
                        any(), any(), any(), any());
    }

    private static Stream<Arguments> serviceRequestStatusAndPaymentMethodAndExceptionMessage() {
        return Stream.of(arguments("Paid", "Payment by account",
                        "Service request payment for Case:1661448513999408 not valid CaseType:WILL_LODGEMENT"),
                arguments("Paid", "Cheque", "Service request payment method for Case:1661448513999408 not valid"),
                arguments(null, "Payment by account", "serviceRequestStatus not a valid value"));
    }
}
