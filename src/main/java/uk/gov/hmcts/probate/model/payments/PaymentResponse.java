package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PaymentResponse {

    private String reference;

    @JsonProperty("date_created")
    private Date dateCreated;

    private String status;

    @JsonProperty("payment_group_reference")
    private String paymentGroupReference;
}
