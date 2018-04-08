package uk.gov.hmcts.probate.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@RestControllerAdvice()
public class JacksonMappingExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(JacksonMappingExceptionHandler.class);

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<FieldErrorResponse> handleMessageNotReadableException(JsonMappingException exception) {

        logger.info("Invalid Payload", keyValue("missingKeys", exception));
        String fieldPath = exception.getPath().stream()
            .map(JsonMappingException.Reference::getFieldName)
            .collect(Collectors.joining("."));

        FieldErrorResponse error = FieldErrorResponse.builder()
            .field(fieldPath)
            .code("JsonParseError")
            .message(exception.getMessage())
            .build();

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
