package uk.gov.hmcts.probate.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class NotificationClientExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String UNABLE_TO_SEND_EMAIL = "Unable to send email";

    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationClientExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(value = NotificationClientException.class)
    public ResponseEntity<CallbackResponse> handle(NotificationClientException exception) {
        log.warn("Notification service exception", exception);
        final List<String> errors = List.copyOf(getNotifyErrors(exception.getMessage()));

        final CallbackResponse errorResponse = CallbackResponse.builder()
                .errors(errors)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(headers)
                .body(errorResponse);
    }

    /**
     * This relies on the behaviour of NotifyClient#performPostRequest passing the connection's error stream as a json
     * string into the exception message, so is liable to break.
     */
    private List<String> getNotifyErrors(final String exMessage) {
        final String exJson = exMessage.replaceFirst("[^{]*", "");

        final List<String> errors = new ArrayList<>();
        errors.add(UNABLE_TO_SEND_EMAIL);

        final JsonNode outerJson;
        try {
            outerJson = objectMapper.readTree(exJson);
        } catch (JsonProcessingException e) {
            return errors;
        }

        if (!outerJson.isObject()) {
            return errors;
        }
        if (!outerJson.has("errors") || !outerJson.get("errors").isArray()) {
            return errors;
        }

        final JsonNode errorsJson = outerJson.get("errors");
        for (final JsonNode errorJson : errorsJson) {
            if (errorJson.isObject() && errorJson.has("message") && errorJson.get("message").isTextual()) {
                final String message = errorJson.get("message").asText();
                errors.add(message);
            }
        }
        return errors;
    }
}
