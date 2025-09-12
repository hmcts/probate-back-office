package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class DynamicRadioList {
    @JsonProperty("value")
    private DynamicRadioListElement value;
    @JsonProperty("list_items")
    private List<DynamicRadioListElement> listItems;

    @JsonIgnore
    public String getValueCode() {
        return value == null ? null : value.getCode();
    }
}
