package uk.gov.hmcts.probate.model.payments;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditAccountPayment {

    private String accountNumber;
    private BigDecimal amount;
    private String caseReference;
    private String ccdCaseNumber;
    private String currency;
    private String customerReference;
    private String description;
    private String organisationName;
    private String service;
    private String siteId;
    private List<PaymentFee> fees;

}
