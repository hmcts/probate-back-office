package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
public class CaveatDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaveatData data;

    @JsonProperty(value = "last_modified")
    private final String[] lastModified;

    @JsonProperty(value = "state")
    private String state;

    @NotNull
    private final Long id;
}
