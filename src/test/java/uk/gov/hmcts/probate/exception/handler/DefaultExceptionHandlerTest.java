package uk.gov.hmcts.probate.exception.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.exception.model.ErrorResponse;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
    private BindingResult bindingResult;

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

        assertEquals(response.getStatusCode(), BAD_REQUEST);
        assertEquals(response.getBody().getError(), DefaultExceptionHandler.CLIENT_ERROR);
        assertEquals(response.getBody().getMessage(), EXCEPTION_MESSAGE);
    }

    @Test
    public void shouldReturnServiceUnavailableForConnectionException() {
        when(connectionException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        ResponseEntity<ErrorResponse> response = underTest.handle(connectionException);

        assertEquals(response.getStatusCode(), SERVICE_UNAVAILABLE);
        assertEquals(response.getBody().getError(), DefaultExceptionHandler.CONNECTION_ERROR);
        assertEquals(response.getBody().getMessage(), EXCEPTION_MESSAGE);
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

        assertEquals(response.getStatusCode(), BAD_REQUEST);
        assertEquals(response.getBody().getError(), DefaultExceptionHandler.INVALID_REQUEST);

        assertEquals(response.getBody().getFieldErrors().get(0), bve1Mock);
        assertEquals(response.getBody().getFieldErrors().get(1), bve2Mock);
    }
}
