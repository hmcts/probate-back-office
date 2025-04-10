package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.stream.Stream;

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

    protected BusinessValidationException(final String userMessage, BusinessValidationException cause) {
        super(cause.getMessage(), cause);
        this.userMessage = userMessage;

        // trust me
        this.additionalMessages = (String[]) Stream.concat(
                Stream.of(cause.userMessage),
                Arrays.stream(cause.additionalMessages))
                .toArray();
    }

    public String getUserMessage() {
        return userMessage;
    }
    
    public String[] getAdditionalMessages() {
        return additionalMessages;
    }
}
