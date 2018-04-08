package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.controller.validation.SolAddEstateDetailsEventGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class SolsAddress {

    @NotNull(groups = {SolAddEstateDetailsEventGroup.class}, message = "{deceasedAddressIsNull}")
    @Size(min = 1, groups = {SolAddEstateDetailsEventGroup.class}, message = "{deceasedAddressIsNull}")
    @JsonProperty(value = "AddressLine1")
    private final String addressLine1;

    @JsonProperty(value = "AddressLine2")
    private final String addressLine2;

    @JsonProperty(value = "AddressLine3")
    private final String addressLine3;

    @JsonProperty(value = "County")
    private final String county;

    @JsonProperty(value = "PostTown")
    private final String postTown;

    @NotNull(groups = {SolAddEstateDetailsEventGroup.class}, message = "{deceasedPostcodeIsNull}")
    @Size(min = 1, groups = {SolAddEstateDetailsEventGroup.class}, message = "{deceasedPostcodeIsNull}")
    @JsonProperty(value = "PostCode")
    private final String postCode;

    @JsonProperty(value = "Country")
    private final String country;
}
