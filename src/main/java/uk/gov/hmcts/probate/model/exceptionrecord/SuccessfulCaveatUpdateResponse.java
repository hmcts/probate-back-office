package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;

import java.util.List;

@Data
@Builder
public class SuccessfulCaveatUpdateResponse {

    @JsonProperty("case_update_details")
    public final ResponseCaveatData caseUpdateDetails;

    @JsonProperty("warnings")
    public final List<String> warnings;

    @JsonProperty("errors")
    public final List<String> errors;
}