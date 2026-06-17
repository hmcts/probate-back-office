package uk.gov.hmcts.probate.exception;

public class DataMigrationException extends RuntimeException {
    public DataMigrationException(String message) {
        super(message);
    }

    public DataMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
