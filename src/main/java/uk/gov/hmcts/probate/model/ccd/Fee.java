package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class Fee implements Serializable {
    private final String paymentMethod;
    private final String solsPBANumber;
    private final String solsPBAPaymentReference;
    private final BigDecimal applicationFee;
    private final BigDecimal amount;
    private final Long extraCopiesOfGrant;
    private final Long outsideUKGrantCopies;
    private final BigDecimal feeForUkCopies;
    private final BigDecimal feeForNonUkCopies;

}
