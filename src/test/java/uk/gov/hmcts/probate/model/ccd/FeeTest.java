package uk.gov.hmcts.probate.model.ccd;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class FeeTest {
    private static final BigDecimal AMOUNT = new BigDecimal(10000);
    private static final BigDecimal APPLICATION_FEE = new BigDecimal(50000);

    private Fee fee;

    @Before
    public void setup() {
        fee = Fee.builder().applicationFee(APPLICATION_FEE)
                .amount(AMOUNT)
                .build();
    }

    @Test
    public void shouldGetAmountInPounds() {
        BigDecimal amount = fee.getAmountInPounds();
        assertEquals(100.00, amount.doubleValue(), 0.001);
    }

    @Test
    public void shouldGetApplicationFeeInPounds() {
        BigDecimal amount = fee.getApplicationFeeInPounds();
        assertEquals(500.00, amount.doubleValue(), 0.001);
    }
}