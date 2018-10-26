package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdoptedRelatives {

    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "relationship")
    private final String relationship;

    @JsonProperty(value = "adoptedInOrOut")
    private final String adoptedInOrOut;

}
