package uk.gov.hmcts.probate.exception;

public class CcdUpdateNotificationException extends RuntimeException {
    public CcdUpdateNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CcdUpdateNotificationException(String message) {
        super(message);
    }
}
