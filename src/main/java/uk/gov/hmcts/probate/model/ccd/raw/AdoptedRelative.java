package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdoptedRelative {

    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "relationship")
    private final String relationship;

    @JsonProperty(value = "adoptedInOrOut")
    private final String adoptedInOrOut;

}
