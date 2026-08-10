package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "ProbateSolsAliasName", generate = true)
@Data
@Builder
public class AliasName {

    @CCD(label = " ")
    @JsonProperty(value = "SolsAliasname")
    private final String solsAliasname;
}
