package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class CaseDetails {

    @Valid
    @JsonProperty(value = "case_data")
    private final CaseData data;

    @JsonProperty(value = "last_modified")
    private final String[] lastModified;

    @JsonProperty(value = "state")
    private final String state;

    @NotNull
    private final Long id;

    private String grantSignatureBase64;

    private String registryAddressLine1;
    private String registryAddressLine2;
    private String registryAddressLine3;
    private String registryAddressLine4;
    private String registryTown;
    private String registryPostcode;
    private String registryTelephone;

    private String ctscTelephone;
}
