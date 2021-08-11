package uk.gov.hmcts.probate.model.fee;

import lombok.Builder;
import lombok.Data;

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
        List<FeeResponse> allFees = Arrays.asList(applicationFeeResponse, ukCopiesFeeResponse,
            overseasCopiesFeeResponse);
        BigDecimal total = BigDecimal.ZERO;
        for (FeeResponse feeResponse : allFees) {
            total = total.add(feeResponse.getFeeAmount());
        }
        return total;
    }

}
