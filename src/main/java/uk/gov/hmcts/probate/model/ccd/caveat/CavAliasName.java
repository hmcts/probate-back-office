package uk.gov.hmcts.probate.model.ccd.caveat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CavAliasName {

    @JsonProperty(value = "Forenames")
    private final String forenames;

    @JsonProperty(value = "Surname")
    private final String lastName;

}
