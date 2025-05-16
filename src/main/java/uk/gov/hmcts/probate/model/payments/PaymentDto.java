package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {
    @JsonProperty(value = "case_reference")
    private String caseReference;
    @JsonProperty(value = "ccd_case_number")
    private String ccdCaseNumber;
    @JsonProperty(value = "status")
    private String status;
}
