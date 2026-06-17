package uk.gov.hmcts.probate.exception;

import lombok.EqualsAndHashCode;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public class JsonObjectUtilsException extends BusinessValidationException {
    final Cause errorCause;

    public JsonObjectUtilsException(
            final String message,
            final Cause errorCause) {
        super("An internal error has occurred when trying to query for matching cases", message);
        this.errorCause = Objects.requireNonNull(errorCause);
    }

    public Cause getErrorCause() {
        return errorCause;
    }

    public enum Cause {
        WRONG_TYPE,
        NO_SUBKEY,
        MISMATCHED_SUBKEY,
    }
}
