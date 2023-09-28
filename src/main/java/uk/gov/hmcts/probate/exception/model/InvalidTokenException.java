package uk.gov.hmcts.probate.exception.model;


public class InvalidTokenException extends Exception {
    public InvalidTokenException(String message) {
        super(message);
    }
}
