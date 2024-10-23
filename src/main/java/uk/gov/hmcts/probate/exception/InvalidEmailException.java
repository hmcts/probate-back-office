package uk.gov.hmcts.probate.exception;

public class InvalidEmailException extends BusinessValidationException {

    public InvalidEmailException(String userMessage, String message) {
        super(userMessage, message);
    }

    public InvalidEmailException(String userMessage, String message, String... additionalMessages) {
        super(userMessage, message, additionalMessages);
    }

}
