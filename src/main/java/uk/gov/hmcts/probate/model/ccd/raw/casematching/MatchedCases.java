package uk.gov.hmcts.probate.model.ccd.raw.casematching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatchedCases {

    @JsonProperty(value = "cases")
    private final List<Case> cases;
}
