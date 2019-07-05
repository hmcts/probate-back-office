package uk.gov.hmcts.probate.exception;

public class InvalidEmailException extends GrantOfRepresentationException {

    public InvalidEmailException(String userMessage, String message) {
        super(userMessage, message);
    }

}
