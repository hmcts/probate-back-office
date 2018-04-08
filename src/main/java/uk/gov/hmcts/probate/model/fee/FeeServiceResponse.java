package uk.gov.hmcts.probate.model.fee;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FeeServiceResponse {
    private final BigDecimal applicationFee;
    private final BigDecimal feeForUkCopies;
    private final BigDecimal feeForNonUkCopies;
    private final BigDecimal total;
}
