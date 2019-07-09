package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessValidationException extends RuntimeException {

    private String userMessage;
    public BusinessValidationException(final String userMessage, final String message) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
