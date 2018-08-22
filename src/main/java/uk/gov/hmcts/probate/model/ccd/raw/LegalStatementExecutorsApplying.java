package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LegalStatementExecutorsApplying {

    @JsonProperty(value = "value")
    private final LegalStatementExecutorApplying value;

    @JsonProperty(value = "id")
    private final String id;

}
