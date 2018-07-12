package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProbateAliasName {

    @JsonProperty(value = "Forenames")
    private final String forenames;

    @JsonProperty(value = "LastName")
    private final String lastName;
}
