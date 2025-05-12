package uk.gov.hmcts.probate.exception.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.service.notify.NotificationClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class NotificationClientExceptionHandlerTest {
    NotificationClientExceptionHandler underTest;

    ObjectMapper objectMapperSpy;

    @BeforeEach
    void setUp() {
        objectMapperSpy = spy(ObjectMapper.class);
        underTest = new NotificationClientExceptionHandler(objectMapperSpy);
    }

    @Test
    void shouldReturnNotificationClientException() {
        final String firstError = "first_error";
        final String secondError = "second_error";
        final String exceptMsg = new StringBuilder()
                .append("Status code: 400 ")
                .append("{\"errors\":[")
                .append("{\"message\":\"").append(firstError).append("\"},")
                .append("{\"message\":\"").append(secondError).append("\"}")
                .append("]}")
                .toString();

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);

        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(3, response.getBody().getErrors().size(), "Expected three errors");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                "expected first error to be from handler");
        assertEquals(firstError, response.getBody().getErrors().get(1),
                "expected second error to match from exception message");
        assertEquals(secondError, response.getBody().getErrors().get(2),
                "expected third error to match from exception message");
    }

    @Test
    void shouldReturnNotificationClientExceptionWhenInvalidJson() throws JsonProcessingException {
        final String exceptMsg = "";

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        when(objectMapperSpy.readTree(anyString())).thenThrow(new JsonParseException(exceptMsg));

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);

        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(1, response.getBody().getErrors().size(),
                "Expected only one error on json parse failure");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                "expected first error to be from handler");
    }

    @Test
    void shouldReturnNotificationClientExceptionWhenJsonArray() throws JsonProcessingException {
        final String exceptMsg = "[]";

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);

        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(1, response.getBody().getErrors().size(),
                "Expected only one error on json parse failure");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                "expected first error to be from handler");
    }

    @Test
    void shouldReturnNotificationClientExceptionWhenJsonMissingErrors() throws JsonProcessingException {
        final String firstError = "first_error";
        final String secondError = "second_error";
        final String exceptMsg = "{}";

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);

        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(1, response.getBody().getErrors().size(),
                "Expected only one error on json parse failure");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                "expected first error to be from handler");
    }

    @Test
    void shouldReturnNotificationClientExceptionWhenJsonErrorsIsObject() throws JsonProcessingException {
        final String firstError = "first_error";
        final String secondError = "second_error";
        final String exceptMsg = "{\"errors\":{}}";

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);

        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(1, response.getBody().getErrors().size(),
                "Expected only one error on json parse failure");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                "expected first error to be from handler");
    }

    @Test
    void shouldReturnNotificationClientExceptionWhenJsonErrorsContainsUnexpected() throws JsonProcessingException {
        final String firstError = "first_error";
        final String secondError = "second_error";
        final String exceptMsg = new StringBuilder()
                .append("{\"errors\":[")
                .append("{\"message\":\"").append(firstError).append("\"},")
                .append("[],")
                .append("{},")
                .append("{\"message\": 0},")
                .append("{\"message\":\"").append(secondError).append("\"}")
                .append("]}")
                .toString();

        NotificationClientException notificationClientException = mock(NotificationClientException.class);
        when(notificationClientException.getMessage()).thenReturn(exceptMsg);

        ResponseEntity<CallbackResponse> response = underTest.handle(notificationClientException);


        assertEquals(OK, response.getStatusCode(), "Expected HTTP OK (200) response status");
        assertEquals(3, response.getBody().getErrors().size(), "Expected three errors");
        assertEquals(NotificationClientExceptionHandler.UNABLE_TO_SEND_EMAIL, response.getBody().getErrors().get(0),
                        "expected first error to be from handler");
        assertEquals(firstError, response.getBody().getErrors().get(1),
                        "expected second error to match from exception message");
        assertEquals(secondError, response.getBody().getErrors().get(2),
                        "expected third error to match from exception message");
    }
}
