package uk.gov.hmcts.probate.model.ccd.standingsearch.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class StandingSearchCallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final StandingSearchDetails caseDetails;
}
