package uk.gov.hmcts.probate.model.ccd.raw.casematching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Data
public class Case {

    @JsonProperty(value = "case_data")
    private final CaseData data;

    private final Long id;
}
