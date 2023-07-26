package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.model.InvalidTokenException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentStatusReponse;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentControllerUnitTest {

    @Mock
    private PaymentsService paymentsServiceMock;

    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;

    @Mock
    private ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDtoMock;

    @Mock
    private CaseDataTransformer caseDataTransformerMock;

    @Mock
    private CallbackRequest request;

    @Mock
    private CallbackResponse callbackResponse;
    @MockBean
    private SecurityUtils authS2sUtil;

    @InjectMocks
    private PaymentController underTest;

    @MockBean
    private AppInsights appInsights;

    private static final String s2sAuthToken = "s2sAuthToken";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldDoGorServiceRequestUpdate() throws InvalidTokenException {
        when(authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken)).thenReturn(true);
        ResponseEntity<PaymentStatusReponse> response = underTest
                                                        .doGorServiceRequestUpdate(s2sAuthToken,
                                                                serviceRequestUpdateResponseDtoMock);
        verify(paymentsServiceMock)
                .updateCaseFromServiceRequest(serviceRequestUpdateResponseDtoMock, CcdCaseType.GRANT_OF_REPRESENTATION);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().status);
    }

    @Test
    void shouldDoCaveatServiceRequestUpdate() throws InvalidTokenException {
        when(authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken)).thenReturn(true);
        ResponseEntity<PaymentStatusReponse> response = underTest
                .doCaveatServiceRequestUpdate(s2sAuthToken, serviceRequestUpdateResponseDtoMock);
        verify(paymentsServiceMock)
                .updateCaseFromServiceRequest(serviceRequestUpdateResponseDtoMock, CcdCaseType.CAVEAT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().status);
    }

    @Test
    void shouldUpdateTaskList() {
        when(callbackResponseTransformerMock.updateTaskList(request)).thenReturn(callbackResponse);
        doNothing().when(caseDataTransformerMock).transformCaseDataForEvidenceHandled(request);
        ResponseEntity<CallbackResponse> response = underTest.updateTaskList(request);

        verify(caseDataTransformerMock).transformCaseDataForEvidenceHandled(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(callbackResponse, response.getBody());
    }

    @Test
    void shouldReturnForbiddenWhenServiceNotAllowedAndGorServiceRequest() throws InvalidTokenException {
        when(authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken)).thenReturn(false);
        ResponseEntity<PaymentStatusReponse> response = underTest
                .doGorServiceRequestUpdate(s2sAuthToken,
                        serviceRequestUpdateResponseDtoMock);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("forbidden", response.getBody().status);
    }

    @Test
    void shouldReturnForbiddenWhenServiceNotAllowedAndCaveatServiceRequest() throws InvalidTokenException {
        when(authS2sUtil.checkIfServiceIsAllowed(s2sAuthToken)).thenReturn(false);
        ResponseEntity<PaymentStatusReponse> response = underTest
                .doGorServiceRequestUpdate(s2sAuthToken,
                        serviceRequestUpdateResponseDtoMock);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("forbidden", response.getBody().status);
    }

    @Test
    void shouldThrowWhenS2sTokenIsMissingOrInvalidWithGorServiceRequest() {
        when(underTest.doGorServiceRequestUpdate(s2sAuthToken,
                        serviceRequestUpdateResponseDtoMock)).thenThrow(InvalidTokenException.class);
        ResponseEntity<PaymentStatusReponse> response = underTest
                .doGorServiceRequestUpdate(s2sAuthToken,
                        serviceRequestUpdateResponseDtoMock);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
