package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentServiceResponse {

    private String serviceRequestReference;

    @JsonCreator
    public PaymentServiceResponse(@JsonProperty("service_request_reference") String serviceRequestReference) {
        this.serviceRequestReference = serviceRequestReference;
    }

    public String getServiceRequestReference() {
        return serviceRequestReference;
    }
}