package uk.gov.hmcts.probate.appinsights;

public enum AppInsightsEvents {
    REQUEST_SENT("Request made to: "),
    REST_CLIENT_EXCEPTION("RestClientException: "),
    ILLEGAL_ARGUMENT_EXCEPTION("IllegalArgumentException: ");

    private String displayName;

    AppInsightsEvents(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
