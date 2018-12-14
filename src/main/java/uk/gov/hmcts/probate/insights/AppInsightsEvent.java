package uk.gov.hmcts.probate.insights;

public enum AppInsightsEvent {
    REQUEST_SENT("Request made to: "),
    REST_CLIENT_EXCEPTION("RestClientException: ");

    private String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}