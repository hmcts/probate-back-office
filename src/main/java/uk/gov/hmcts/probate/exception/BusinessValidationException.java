package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessValidationException extends RuntimeException {

    private final String userMessage;
    private final String[] additionalMessages;

    public BusinessValidationException(final String userMessage, final String message) {
        super(message);
        this.userMessage = userMessage;
        this.additionalMessages = new String[]{};
    }

    public BusinessValidationException(final String userMessage, final String message, String... additionalMessages) {
        super(message);
        this.userMessage = userMessage;
        this.additionalMessages = additionalMessages;
    }

    public String getUserMessage() {
        return userMessage;
    }
    
    public String[] getAdditionalMessages() {
        return additionalMessages;
    }
}
