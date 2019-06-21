package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReissueReason {

    @JsonProperty(value = "reissueReason")
    private final String reason;
    @JsonProperty(value = "reissueReasonDetails")
    private final String details;
}
