package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LanguagePreference {

    ENGLISH("english"),
    WELSH("welsh");

    private final String code;

    LanguagePreference(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
