package uk.gov.hmcts.probate.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.validation.FieldError;

@Builder
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorResponse {
    private final String param;
    private final String field;
    private final String code;
    private final String message;

    public static FieldErrorResponse of(FieldError fieldError) {
        return FieldErrorResponse.builder()
            .param(fieldError.getObjectName())
            .field(fieldError.getField())
            .code(fieldError.getCode())
            .message(fieldError.getDefaultMessage())
            .build();
    }
}
