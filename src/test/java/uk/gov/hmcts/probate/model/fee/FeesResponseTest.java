package uk.gov.hmcts.probate.model.fee;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class FeesResponseTest {

    @Test
    public void shouldGetTotalAmount() {
        FeesResponse feesResponse = FeesResponse.builder()
            .applicationFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(215)).code("appCode")
                .description("appDesc").build())
            .ukCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(1)).code("ukCopiesCode")
                .description("ukCopiesDesc").build())
            .overseasCopiesFeeResponse(FeeResponse.builder().feeAmount(BigDecimal.valueOf(2)).code("osCopiesCode")
                .description("osCopiesDesc").build())
            .build();
        BigDecimal total = feesResponse.getTotalAmount();
        assertEquals(218L, total.longValue());
    }
}