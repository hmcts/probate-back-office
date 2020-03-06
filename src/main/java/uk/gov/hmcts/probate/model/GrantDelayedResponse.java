package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class GrantDelayedResponse {

    private List<String> delayResponseData;

    @JsonCreator
    public GrantDelayedResponse(@JsonProperty("delayResponseData") List<String> delayResponseData) {
        this.delayResponseData = delayResponseData;
    }

    public List<String> getDelayResponseData() {
        return delayResponseData;
    }

}