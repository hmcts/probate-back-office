package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;

@Data
@Builder
public class ResponseCaveatDetails {
    
    @JsonProperty("case_data")
    public final ResponseCaveatData caseData;

}
