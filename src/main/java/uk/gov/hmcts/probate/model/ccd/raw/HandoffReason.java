package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HandoffReason {

    @JsonProperty(value = "caseHandoffReason")
    private final String caseHandoffReason;
}
