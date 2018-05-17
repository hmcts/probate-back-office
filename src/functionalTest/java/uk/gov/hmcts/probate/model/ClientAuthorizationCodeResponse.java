package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAuthorizationCodeResponse {
    public String code;

    ClientAuthorizationCodeResponse() {
    }
}