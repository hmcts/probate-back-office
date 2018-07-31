package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AliasName {

    @JsonProperty(value = "SolsAliasname")
    private final String solsAliasname;

    private ProbateAliasName aliasName;

}
