package uk.gov.hmcts.probate.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.exception.model.ErrorResponse;
import uk.gov.service.notify.NotificationClientException;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestControllerAdvice
@ControllerAdvice
class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public static final String INVALID_REQUEST = "Invalid Request";
    public static final String CLIENT_ERROR = "Client Error";
    public static final String CONNECTION_ERROR = "Connection error";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle(BadRequestException exception) {

        log.info("Invalid Payload", keyValue("missingKeys", exception.getErrors()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), INVALID_REQUEST, exception.getMessage());
        errorResponse.setFieldErrors(exception.getErrors());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, BAD_REQUEST);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handle(ClientException exception) {
        log.warn("Client exception, response code: {}", exception.getStatusCode(), exception);

        ErrorResponse errorResponse = new ErrorResponse(exception.getStatusCode(), CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

    @ExceptionHandler(ConnectionException.class)
    public ResponseEntity<ErrorResponse> handle(ConnectionException exception) {
        log.warn("Can't connect to service, response code: {}", exception.getMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse(SERVICE_UNAVAILABLE.value(), CONNECTION_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = NotificationClientException.class)
    public ResponseEntity<ErrorResponse> handle(NotificationClientException exception) {
        log.warn("Notification service exception", exception);
        ErrorResponse errorResponse = new ErrorResponse(SERVICE_UNAVAILABLE.value(), CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, SERVICE_UNAVAILABLE);
    }
}
