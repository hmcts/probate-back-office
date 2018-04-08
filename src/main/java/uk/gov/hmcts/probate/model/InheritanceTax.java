package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonRootName("iht")
public class InheritanceTax implements Serializable {

    @NotNull(message = "{ihtNetIsNull}")
    @DecimalMin(value = "0.0", message = "{ihtNetNegative}")
    @JsonProperty("netValue")
    private final Float netValue;

    @NotNull(message = "{ihtGrossIsNull}")
    @DecimalMin(value = "0.0", message = "{ihtGrossNegative}")
    @JsonProperty("grossValue")
    private final Float grossValue;

    @JsonCreator
    public InheritanceTax(@JsonProperty("netValue") Float netValue,
                          @JsonProperty("grossValue") Float grossValue) {
        this.netValue = netValue;
        this.grossValue = grossValue;
    }

    public Float getNetValue() {
        return netValue;
    }

    public Float getGrossValue() {
        return grossValue;
    }

}
