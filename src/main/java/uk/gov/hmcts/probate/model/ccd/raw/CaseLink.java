package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CaseLink implements Serializable {

    @JsonProperty(value = "CaseReference")
    private final String caseReference;
}
