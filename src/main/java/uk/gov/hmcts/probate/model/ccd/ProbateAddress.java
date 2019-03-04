package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProbateAddress implements Serializable {

    @JsonProperty(value = "AddressLine1")
    private String proAddressLine1;

    @JsonProperty(value = "AddressLine2")
    private String proAddressLine2;

    @JsonProperty(value = "AddressLine3")
    private String proAddressLine3;

    @JsonProperty(value = "County")
    private String proCounty;

    @JsonProperty(value = "PostTown")
    private String proPostTown;

    @JsonProperty(value = "PostCode")
    private String proPostCode;

    @JsonProperty(value = "Country")
    private String proCountry;
}
