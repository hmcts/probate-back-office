package uk.gov.hmcts.probate.exception;

public class BulkPrintException extends GrantOfRepresentationException {

    public BulkPrintException(String userMessage, String message) {
        super(userMessage, message);
    }

}
