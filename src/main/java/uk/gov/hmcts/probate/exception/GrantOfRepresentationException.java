package uk.gov.hmcts.probate.exception;

public class GrantOfRepresentationException extends RuntimeException {
    private final String userMessage;

    public GrantOfRepresentationException(String userMessage, String message) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
