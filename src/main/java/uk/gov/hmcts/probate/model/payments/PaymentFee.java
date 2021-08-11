package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentFee {

    @JsonProperty(value = "calculated_amount")
    private BigDecimal calculatedAmount;
    private String code;
    private String description;
    @JsonProperty(value = "fee_amount")
    private BigDecimal feeAmount;
    private String jurisdiction1;
    private String jurisdiction2;
    private String version;
    private BigDecimal volume;

}
