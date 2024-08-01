package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
public class ReturnedCaveatDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaveatData data;

    private final String[] lastModified;

    @NotNull
    private final Long id;
}
