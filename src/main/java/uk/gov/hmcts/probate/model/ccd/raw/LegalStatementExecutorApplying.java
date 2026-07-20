package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Data
@Builder
public class LegalStatementExecutorApplying {

    @CCD(label = "Name")
    private final String name;

    @CCD(label = "Sign")
    @JsonProperty(value = "sign")
    private final String sign;

}
