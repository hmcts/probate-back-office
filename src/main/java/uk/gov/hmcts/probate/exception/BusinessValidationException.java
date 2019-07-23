package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(final String message) {
        super(message);
    }
}
