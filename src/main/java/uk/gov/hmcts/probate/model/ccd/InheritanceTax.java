package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class InheritanceTax implements Serializable {

    private final String formName;
    private final Float netValue;
    private final Float grossValue;

    public BigDecimal getNetValueInPounds() {
        return BigDecimal.valueOf(netValue).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }
}
