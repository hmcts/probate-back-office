package uk.gov.hmcts.probate.model.ccd.caveat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class CaveatCallbackResponse {

    private ResponseCaveatData data;
    private List<String> errors;
    private List<String> warnings;

}
