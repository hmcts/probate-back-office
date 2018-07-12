package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalExecutorsApplying {

    @JsonProperty(value = "value")
    private final AdditionalExecutorApplying additionalExecutorApplying;

    @JsonProperty(value = "id")
    private final String id;
}
