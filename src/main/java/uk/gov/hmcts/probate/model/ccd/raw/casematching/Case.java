package uk.gov.hmcts.probate.model.ccd.raw.casematching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Case {

    @JsonProperty(value = "case_data")
    private final CaseData data;

    private final Long id;

    @JsonProperty(value = "case_type_id")
    private final String caseTypeId;
}
