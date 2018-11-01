package uk.gov.hmcts.probate.model.ccd.caveat.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class CaveatCallbackResponse {

    private ResponseCaveatData caveatData;
    private List<String> errors;
    private List<String> warnings;

}
