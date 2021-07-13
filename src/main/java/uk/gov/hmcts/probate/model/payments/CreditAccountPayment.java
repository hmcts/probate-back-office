package uk.gov.hmcts.probate.model.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditAccountPayment {

    @JsonProperty(value = "account_number")
    private String accountNumber;
    private BigDecimal amount;
    @JsonProperty(value = "case_reference")
    private String caseReference;
    @JsonProperty(value = "ccd_case_number")
    private String ccdCaseNumber;
    private String currency;
    @JsonProperty(value = "customer_reference")
    private String customerReference;
    private String description;
    @JsonProperty(value = "organisation_name")
    private String organisationName;
    private String service;
    @JsonProperty(value = "site_id")
    private String siteId;
    private List<PaymentFee> fees;

}
