package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Data
@Builder
public class InheritanceTax implements Serializable {

    private final String formName;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;

    public BigDecimal getNetValueInPounds() {
        log.info("netValue:{}", netValue.toString());
        return netValue.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }
}
