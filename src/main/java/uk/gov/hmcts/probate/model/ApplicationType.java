package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ApplicationType {
    @JsonProperty("Solicitor")
    SOLICITOR("sol", "Solicitor"),

    @JsonProperty("Personal")
    PERSONAL("pa", "Personal");

    private final String code;
    private final String value;

    ApplicationType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public static ApplicationType fromString(String text) {
        for (ApplicationType applicationType : ApplicationType.values()) {
            if (applicationType.value.equalsIgnoreCase(text)) {
                return applicationType;
            }
        }
        return null;
    }
}
