package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.Valid;

@Data
public class CaveatCallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final CaveatDetails caseDetails;
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("case_details_before")
    private CaveatDetails caseDetailsBefore;
}
