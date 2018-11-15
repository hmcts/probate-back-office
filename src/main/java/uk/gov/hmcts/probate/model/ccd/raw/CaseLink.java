package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseLink {

    @JsonProperty(value = "FieldId")
    private final CaseReference fieldId;
}
