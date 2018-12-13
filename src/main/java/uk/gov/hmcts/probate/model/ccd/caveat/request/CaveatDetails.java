package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class CaveatDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaveatData caveatData;

    @JsonProperty(value = "last_modified")
    private final String[] lastModified;

    @NotNull
    private final Long id;
}
