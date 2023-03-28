package uk.gov.hmcts.probate.model.payments.servicerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceRequestUpdateResponseDto {
    @JsonProperty(value = "service_request_reference")
    private String serviceRequestReference;
    @JsonProperty(value = "ccd_case_number")
    private String ccdCaseNumber;
    @JsonProperty(value = "service_request_amount")
    private BigDecimal serviceRequestAmount;
    @JsonProperty(value = "service_request_status")
    private String serviceRequestStatus;
    @JsonProperty(value = "payment")
    private ServiceRequestPaymentResponseDto serviceRequestPaymentResponseDto;
}
