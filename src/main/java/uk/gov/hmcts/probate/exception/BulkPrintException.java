package uk.gov.hmcts.probate.exception;

public class BulkPrintException extends BusinessValidationException {

    public BulkPrintException(String userMessage, String message) {
        super(userMessage, message);
    }

    public BulkPrintException(String userMessage, String message, String... additionalMessages) {
        super(userMessage, message, additionalMessages);
    }
}
