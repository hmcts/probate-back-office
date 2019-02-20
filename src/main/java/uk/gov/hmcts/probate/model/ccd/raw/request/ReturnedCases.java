package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ReturnedCases {

    @JsonProperty(value = "cases")
    private final List<Case> cases;
}
