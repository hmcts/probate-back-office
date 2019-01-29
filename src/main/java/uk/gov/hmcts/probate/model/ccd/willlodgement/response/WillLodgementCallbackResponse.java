package uk.gov.hmcts.probate.model.ccd.willlodgement.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class WillLodgementCallbackResponse {

    @JsonProperty(value = "data")
    private ResponseWillLodgementData responseWillLodgementData;
    private List<String> errors;
    private List<String> warnings;
}
