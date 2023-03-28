package uk.gov.hmcts.probate.model.payments.servicerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.util.List;

@Data
@Builder
public class ServiceRequestDto {
    @JsonProperty(value = "call_back_url")
    private String callbackUrl;
    @JsonProperty(value = "case_payment_request")
    private CasePaymentRequestDto casePaymentRequest;
    @JsonProperty(value = "case_reference")
    private String caseReference;
    @JsonProperty(value = "ccd_case_number")
    private String ccdCaseNumber;
    private List<PaymentFee> fees;
    @JsonProperty(value = "hmcts_org_id")
    private String hmctsOrgId;
}
