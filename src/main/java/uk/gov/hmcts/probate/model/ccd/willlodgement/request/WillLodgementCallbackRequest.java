package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class WillLodgementCallbackRequest {

    @Valid
    @JsonProperty(value = "case_details", required = true)
    private final WillLodgementDetails caseDetails;
}
