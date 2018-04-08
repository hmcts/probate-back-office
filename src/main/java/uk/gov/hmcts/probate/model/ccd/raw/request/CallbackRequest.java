package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final CaseDetails caseDetails;
}
