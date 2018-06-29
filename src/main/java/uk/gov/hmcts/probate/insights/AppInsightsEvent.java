package uk.gov.hmcts.probate.insights;

public enum AppInsightsEvent {
    REQUEST_SENT("Request made to: ");

    private String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}