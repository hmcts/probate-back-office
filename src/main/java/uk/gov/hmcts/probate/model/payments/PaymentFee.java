package uk.gov.hmcts.probate.model.payments;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.fee.FeeResponse;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentFee {

    //        private BigDecimal allocated_amount;
//        private BigDecimal amount_due;
//        private BigDecimal apportion_amount;
//        private BigDecimal apportioned_payment;
//        private BigDecimal calculated_amount;
    private String case_reference;
    private String ccd_case_number;
    private String code;
    //        private LocalDate date_apportioned;
//        private LocalDate date_created;
//        private LocalDate date_receipt_processed;
//        private LocalDate date_updated;
    private String description;
    private BigDecimal fee_amount;
    private Long id;
    private String jurisdiction1;
    private String jurisdiction2;
    //        private String memo_line;
//        private String natural_account_code;
//        private BigDecimal net_amount;
//        private String payment_group_reference;
    private String reference;
    private String version;
    private BigDecimal volume;

    public static PaymentFee buildPaymentFee(FeeResponse feeResponse) {
        return PaymentFee.builder()
            .case_reference("")
            .ccd_case_number("")
            .code(feeResponse.getCode())
            .description("")
            .fee_amount(feeResponse.getFeeAmount())
            .jurisdiction1("jurisdiction1")
            .jurisdiction2("jurisdiction2")
            .reference("reference")
            .version(feeResponse.getVersion())
            .volume(BigDecimal.ZERO)
            .build();
    }
}
