package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Accessors(chain = true)
public class CallbackResponse {

    private ResponseCaseData data;
    private List<String> errors;
    private List<String> warnings;
    @JsonProperty("supplementary_data_request")
    private Map<String, Map<String, Object>> supplementary_data_request;

}
