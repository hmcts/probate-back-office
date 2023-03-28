package uk.gov.hmcts.probate.model.payments.servicerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceRequestPaymentResponseDto {

    @JsonProperty("payment_amount")
    private BigDecimal paymentAmount;
    @JsonProperty("payment_reference")
    private String paymentReference;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("case_reference")
    private String caseReference;
    @JsonProperty("account_number")
    private String accountNumber;
}
