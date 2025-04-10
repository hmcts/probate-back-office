package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaymentsResponse {
    @JsonProperty("payments")
    private List<PaymentDto> payments;
}
