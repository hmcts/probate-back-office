package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaperApplicationException extends BusinessValidationException {

    public PaperApplicationException(String userMessage, String message) {
        super(userMessage, message);
    }

    public PaperApplicationException(String userMessage, String message, String... userMessageParameters) {
        super(userMessage, message, userMessageParameters);
    }
}
