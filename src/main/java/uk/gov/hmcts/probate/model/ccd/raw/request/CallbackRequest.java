package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.Valid;

@Data
public class CallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final CaseDetails caseDetails;
    @JsonProperty("event_id")
    private String eventId;
}
