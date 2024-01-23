package uk.gov.hmcts.probate.functional.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAuthorizationResponse {
    @JsonProperty("access_token")
    public String accessToken;

    ClientAuthorizationResponse() {
    }
}