package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
public class WillLodgementDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final WillLodgementData data;

    @JsonProperty(value = "last_modified")
    private final String[] lastModified;

    @NotNull
    private final Long id;
    
    private String grantSignatureBase64;

    private String newcastleRegistryAddress;
}
