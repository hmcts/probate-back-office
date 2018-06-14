package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ApplicationType {
    @JsonProperty("Solicitor")
    SOLICITOR,

    @JsonProperty("Personal")
    PERSONAL
}
