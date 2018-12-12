package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProbateAddress implements Serializable {

    @JsonProperty(value = "AddressLine1")
    private final String proAddressLine1;

    @JsonProperty(value = "AddressLine2")
    private final String proAddressLine2;

    @JsonProperty(value = "AddressLine3")
    private final String proAddressLine3;

    @JsonProperty(value = "County")
    private final String proCounty;

    @JsonProperty(value = "PostTown")
    private final String proPostTown;

    @JsonProperty(value = "PostCode")
    private final String proPostCode;

    @JsonProperty(value = "Country")
    private final String proCountry;
}
