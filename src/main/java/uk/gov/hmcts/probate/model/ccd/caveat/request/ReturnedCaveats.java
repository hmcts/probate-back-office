package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReturnedCaveats {

    @JsonProperty(value = "cases")
    private final List<ReturnedCaveatDetails> caveats;
}
