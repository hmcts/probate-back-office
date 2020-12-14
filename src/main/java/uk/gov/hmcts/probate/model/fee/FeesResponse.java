package uk.gov.hmcts.probate.model.fee;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class FeesResponse {

    private FeeResponse applicationFeeResponse;
    private FeeResponse ukCopiesFeeResponse;
    private FeeResponse overseasCopiesFeeResponse;

    public BigDecimal getTotalAmount() {
        List<FeeResponse> paymentFees = Arrays.asList(applicationFeeResponse, ukCopiesFeeResponse, overseasCopiesFeeResponse);
        BigDecimal total = BigDecimal.ZERO;
        for (FeeResponse paymentFee: paymentFees){
            total = total.add(paymentFee.getFeeAmount());
        }
        return total;
    }
    
}
