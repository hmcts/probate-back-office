package uk.gov.hmcts.probate.model.fee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeResponse {

    @JsonProperty(value = "fee_amount")
    private BigDecimal feeAmount;
    private String description;
    private String version;
    private String code;

}
