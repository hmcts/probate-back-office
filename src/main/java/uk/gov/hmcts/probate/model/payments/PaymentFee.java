package uk.gov.hmcts.probate.model.payments;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentFee {

    private BigDecimal calculated_amount;
    private String code;
    private String description;
    private BigDecimal fee_amount;
    private String jurisdiction1;
    private String jurisdiction2;
    private String version;
    private BigDecimal volume;

}
