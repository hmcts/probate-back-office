package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaseMatchingException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String message;
}
