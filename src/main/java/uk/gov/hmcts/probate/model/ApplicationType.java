package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ApplicationType {
    @JsonProperty("Solicitor")
    SOLICITOR("sol"),

    @JsonProperty("Personal")
    PERSONAL("pa");

    private final String code;

    ApplicationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
