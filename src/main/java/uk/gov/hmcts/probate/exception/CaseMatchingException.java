package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatusCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaseMatchingException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String message;
}
