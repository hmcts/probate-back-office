package uk.gov.hmcts.probate.model.ccd.standingsearch.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class StandingSearchCallbackResponse {

    @JsonProperty(value = "data")
    private ResponseStandingSearchData responseStandingSearchData;
    private List<String> errors;
    private List<String> warnings;
}
