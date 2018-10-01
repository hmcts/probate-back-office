package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class StopReason {

    @JsonProperty(value = "caseStopReason")
    private final String caseStopReason;
}
