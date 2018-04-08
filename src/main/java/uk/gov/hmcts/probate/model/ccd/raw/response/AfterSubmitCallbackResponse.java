package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AfterSubmitCallbackResponse {

    @JsonProperty(value = "confirmation_header")
    private final String confirmationHeader;

    @JsonProperty(value = "confirmation_body")
    private final String confirmationBody;
}
