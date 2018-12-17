package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstateItem {

    @JsonProperty(value = "item")
    private final String item;

    @JsonProperty(value = "value")
    private final String value;

}
