package uk.gov.hmcts.probate.model.fee;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeesResponse {

    private FeeResponse applicationFeeResponse;
    private FeeResponse ukCopiesFeeResponse;
    private FeeResponse overseasCopiesFeeResponse;
}
