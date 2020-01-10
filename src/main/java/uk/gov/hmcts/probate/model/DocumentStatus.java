package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum DocumentStatus {

    @JsonProperty("final")
    FINAL("final"),

    @JsonProperty("preview")
    PREVIEW("preview");

    private final String statusName;

    DocumentStatus(String statusName) {
        this.statusName = statusName;
    }
}
