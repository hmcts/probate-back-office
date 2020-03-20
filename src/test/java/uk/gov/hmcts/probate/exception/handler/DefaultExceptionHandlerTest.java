package uk.gov.hmcts.probate.exception.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.exception.model.ErrorResponse;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class DefaultExceptionHandlerTest {

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

    @InjectMocks
    private DefaultExceptionHandler underTest;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldPassUpstreamBadRequestBackAsIs() {
        when(clientException.getStatusCode()).thenReturn(BAD_REQUEST.value());
        when(clientException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(clientException);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void shouldReturnServiceUnavailableForConnectionException() {
        when(connectionException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(connectionException);

        assertEquals(SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CONNECTION_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void shouldHandleMissingPDFDataAsStatusUN() {
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
    public void shouldReturnNotificationClientException() {
        when(notificationClientException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(notificationClientException);

        assertEquals(SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void shouldReturnBusinessValidationException() {
        when(businessValidationException.getUserMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<CallbackResponse> response = underTest.handle(businessValidationException);

        assertEquals(OK, response.getStatusCode());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getErrors().get(0));
    }

    @Test
    public void shouldReturnNotFoundException() {
        when(notFoundException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(notFoundException);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(DefaultExceptionHandler.CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }
}
