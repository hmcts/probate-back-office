package uk.gov.hmcts.probate.model.fee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fee {

    @JsonProperty(value = "fee_amount")
    private BigDecimal feeAmount;
}
