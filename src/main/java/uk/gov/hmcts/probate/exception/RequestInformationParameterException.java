package uk.gov.hmcts.probate.exception;

public class RequestInformationParameterException extends BusinessValidationException {
    private static final String INVALID_PERSONALISATION_ERROR_MESSAGE =
            "Markdown Link detected in case data, stop sending notification email.";

    public RequestInformationParameterException() {
        super(INVALID_PERSONALISATION_ERROR_MESSAGE, INVALID_PERSONALISATION_ERROR_MESSAGE);
    }
}
