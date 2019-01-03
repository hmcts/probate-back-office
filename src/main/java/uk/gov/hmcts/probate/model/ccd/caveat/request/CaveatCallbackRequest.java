package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CaveatCallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final CaveatDetails caveatDetails;
}
