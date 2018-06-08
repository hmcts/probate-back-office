package uk.gov.hmcts.probate.insights;

public enum AppInsightsEvent {
    CBR_RECEIVED("Call Back Request Received"),
    BINDING_EXCEPTION("Binding Exception occurred");

    private String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}