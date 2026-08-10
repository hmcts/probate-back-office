package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "estateItems", generate = true)
@Data
@Builder
public class EstateItem {

    @CCD(label = "Item")
    @JsonProperty(value = "item")
    private final String item;

    @CCD(label = "Value", typeOverride = FieldType.MoneyGBP)
    @JsonProperty(value = "value")
    private final String value;

}
