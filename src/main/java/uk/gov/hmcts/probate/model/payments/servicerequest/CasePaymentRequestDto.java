package uk.gov.hmcts.probate.model.payments.servicerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CasePaymentRequestDto {
    @JsonProperty(value = "action")
    private String action;
    @JsonProperty(value = "responsible_party")
    private String responsibleParty;

}
