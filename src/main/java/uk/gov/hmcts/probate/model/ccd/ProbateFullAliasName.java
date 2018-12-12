package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbateFullAliasName {

    @JsonProperty(value = "FullAliasName")
    private final String fullAliasName;
}
