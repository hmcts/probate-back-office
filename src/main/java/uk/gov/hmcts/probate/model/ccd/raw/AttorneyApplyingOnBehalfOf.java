package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "AttorneyNamesAndAddress", generate = true)
@Data
@Builder
@AllArgsConstructor
public class AttorneyApplyingOnBehalfOf {

    @CCD(label = "Name")
    @JsonProperty(value = "name")
    private final String name;

    @CCD(label = "Address", typeOverride = FieldType.AddressUK, typeParameterOverride = "Address")
    @JsonProperty(value = "address")
    private final SolsAddress address;

}
