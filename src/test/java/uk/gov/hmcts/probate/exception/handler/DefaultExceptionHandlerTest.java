package uk.gov.hmcts.probate.exception.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.exception.OCRException;
import uk.gov.hmcts.probate.exception.model.ErrorResponse;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

class DefaultExceptionHandlerTest {

    private static final String EXCEPTION_MESSAGE = "Message";

    @Mock
    private ClientException clientException;

    @Mock
    private ConnectionException connectionException;

    @Mock
    private BadRequestException badRequestException;

    @Mock
    private NotificationClientException notificationClientException;

    @Mock
    private BusinessValidationException businessValidationException;

    @Mock
    private NotFoundException notFoundException;

    @Mock
    private OCRMappingException ocrMappingException;
    @Mock
    private OCRException ocrException;

    @InjectMocks
    private DefaultExceptionHandler underTest;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldPassUpstreamBadRequestBackAsIs() {
        when(clientException.getStatusCode()).thenReturn(BAD_REQUEST.value());
        when(clientException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(clientException);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    void shouldReturnServiceUnavailableForConnectionException() {
        when(connectionException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(connectionException);

        assertEquals(SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CONNECTION_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    void shouldHandleMissingPDFDataAsStatusUN() {
        final FieldErrorResponse bve1Mock = FieldErrorResponse.builder()
            .param("Object")
            .field("field1")
            .message("message")
            .build();

        final FieldErrorResponse bve2Mock = FieldErrorResponse.builder()
            .param("Object")
            .field("field2")
            .message("message")
            .build();

        when(badRequestException.getErrors()).thenReturn(Arrays.asList(bve1Mock, bve2Mock));

        ResponseEntity<ErrorResponse> response = underTest.handle(badRequestException);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.INVALID_REQUEST, response.getBody().getError());

        assertEquals(bve1Mock, response.getBody().getFieldErrors().get(0));
        assertEquals(bve2Mock, response.getBody().getFieldErrors().get(1));
    }

    @Test
    void shouldReturnNotificationClientException() {
        when(notificationClientException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(notificationClientException);

        assertEquals(SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    void shouldReturnBusinessValidationException() {
        when(businessValidationException.getUserMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<CallbackResponse> response = underTest.handle(businessValidationException);

        assertEquals(OK, response.getStatusCode());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getErrors().get(0));
    }

    @Test
    void shouldReturnBusinessValidationExceptionWithMultipleErrors() {
        when(businessValidationException.getUserMessage()).thenReturn(EXCEPTION_MESSAGE);
        String[] arr = {"message1", "message2", "message3"};
        when(businessValidationException.getAdditionalMessages()).thenReturn(arr);

        ResponseEntity<CallbackResponse> response = underTest.handle(businessValidationException);

        assertEquals(OK, response.getStatusCode());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getErrors().get(0));
        assertEquals("message1", response.getBody().getErrors().get(1));
        assertEquals("message2", response.getBody().getErrors().get(2));
        assertEquals("message3", response.getBody().getErrors().get(3));
    }

    @Test
    void shouldReturnNotFoundException() {
        when(notFoundException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(notFoundException);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    void shouldReturnOCRMappingException() {
        when(ocrMappingException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ValidationResponse> response = underTest.handle(ocrMappingException);

        assertEquals(OK, response.getStatusCode());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("Message", response.getBody().getErrors().get(0));
    }


    @Test
    void shouldReturnSocketException() {
        when(ocrException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<SuccessfulTransformationResponse> response = underTest.handle(ocrException);

        assertEquals(OK, response.getStatusCode());
    }
}
