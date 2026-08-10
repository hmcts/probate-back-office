package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.model.ccd.raw.request.AdoptedInOrOutFixedList;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "adopted", generate = true)
@Data
@Builder
public class AdoptedRelative {

    @CCD(label = "Name")
    @JsonProperty(value = "name")
    private final String name;

    @CCD(label = "Relationship ")
    @JsonProperty(value = "relationship")
    private final String relationship;

    @CCD(
            label = "Adopted In or Out",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "adoptedInOrOutFixedList",
            typeParameterClass = AdoptedInOrOutFixedList.class
    )
    @JsonProperty(value = "adoptedInOrOut")
    private final String adoptedInOrOut;

}
