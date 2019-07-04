package uk.gov.hmcts.probate.exception;

public class InvalidEmailException extends RuntimeException {
    private final String userMessage;

    public InvalidEmailException(String userMessage, String message) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
