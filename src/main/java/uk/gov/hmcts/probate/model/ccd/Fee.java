package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class Fee implements Serializable {
    private final String paymentMethod;
    private final BigDecimal applicationFee;
    private final BigDecimal amount;
    private final Long extraCopiesOfGrant;
    private final Long outsideUKGrantCopies;
    private final String paymentReferenceNumber;

    public BigDecimal getAmountInPounds() {
        return amount.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getApplicationFeeInPounds() {
        return applicationFee.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

}
