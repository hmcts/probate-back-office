package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StopReasons {
    @JsonProperty(value = "value")
    private final StopReason stopReason;

    @JsonProperty(value = "id")
    private final String id;

}
