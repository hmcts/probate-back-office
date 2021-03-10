package uk.gov.hmcts.probate.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.exception.NotFoundException;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.exception.model.ErrorResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponse;
import uk.gov.hmcts.probate.model.ccd.ocr.ValidationResponseStatus;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@ControllerAdvice
class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INVALID_REQUEST = "Invalid Request";
    public static final String CLIENT_ERROR = "Client Error";
    public static final String CONNECTION_ERROR = "Connection error";
    public static final String UNAUTHORISED_DATA_EXTRACT_ERROR = "Unauthorised access to Data-Extract error";


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle(BadRequestException exception) {

        log.info("Invalid Payload", keyValue("missingKeys", exception.getErrors()));
        ErrorResponse errorResponse =
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), INVALID_REQUEST, exception.getMessage());
        errorResponse.setFieldErrors(exception.getErrors());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, BAD_REQUEST);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handle(ClientException exception) {
        log.warn("Client exception, response code: {}", exception.getStatusCode(), exception);

        ErrorResponse errorResponse =
            new ErrorResponse(exception.getStatusCode(), CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<CallbackResponse> handle(BusinessValidationException exception) {
        log.warn(exception.getMessage());
        List<String> userMessages = new ArrayList<>();
        userMessages.add(exception.getUserMessage());
        if (exception.getAdditionalMessages() != null) {
            Collections.addAll(userMessages, Arrays.copyOf(exception.getAdditionalMessages(),
                exception.getAdditionalMessages().length));
        }
        CallbackResponse callbackResponse = CallbackResponse.builder()
            .errors(userMessages)
            .build();
        return ResponseEntity.ok(callbackResponse);
    }

    @ExceptionHandler(ConnectionException.class)
    public ResponseEntity<ErrorResponse> handle(ConnectionException exception) {
        log.warn("Can't connect to service, response code: {}", exception.getMessage(), exception);
        ErrorResponse errorResponse =
            new ErrorResponse(SERVICE_UNAVAILABLE.value(), CONNECTION_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = NotificationClientException.class)
    public ResponseEntity<ErrorResponse> handle(NotificationClientException exception) {
        log.warn("Notification service exception", exception);
        ErrorResponse errorResponse =
            new ErrorResponse(SERVICE_UNAVAILABLE.value(), CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException exception) {
        log.warn("Not found exception", exception);
        ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND.value(), CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, NOT_FOUND);
    }

    @ExceptionHandler(OCRMappingException.class)
    public ResponseEntity<ValidationResponse> handle(OCRMappingException exception) {
        log.error("An error has occured during the bulk scanning OCR validation process: {}", exception.getMessage(),
            exception);
        List<String> errors = Arrays.asList(exception.getMessage());
        ValidationResponse validationResponse =
            ValidationResponse.builder().status(ValidationResponseStatus.ERRORS).errors(errors).build();
        return ResponseEntity.ok(validationResponse);
    }

}
