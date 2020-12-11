package uk.gov.hmcts.probate.service.payments;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreditAccountPaymentTransformer {

    public CreditAccountPayment transform(CaseDetails caseDetails, FeesResponse feesResponse) {
        List<PaymentFee> paymentFees = buildFees(feesResponse);
        CreditAccountPayment creditAccountPayment = CreditAccountPayment.builder()
            .accountNumber(caseDetails.getData().getSolsPBANumber().getValue().getCode())
            .fees(paymentFees)
            //.caseReference()
            .amount(getTotalAmount(paymentFees))
            .ccdCaseNumber(caseDetails.getId().toString())
            .currency("GBP")
            .description("desc")
            .organisationName("org")
            .service("probate_backend")
            .siteId("siteId")
            .customerReference(caseDetails.getData().getSolsSolicitorAppReference())
            .build();
        
        return creditAccountPayment;
    }

    private BigDecimal getTotalAmount(List<PaymentFee> paymentFees) {
        BigDecimal total = BigDecimal.ZERO;
        for (PaymentFee paymentFee: paymentFees){
            total = total.add(paymentFee.getFee_amount());
        }
        return total;
    }

    private List<PaymentFee> buildFees(FeesResponse feesResponse) {
        ArrayList<PaymentFee> paymentFees = new ArrayList<>();
        PaymentFee applicationFee = PaymentFee.buildPaymentFee(feesResponse.getApplicationFeeResponse());
        PaymentFee ukCopiesFee = PaymentFee.buildPaymentFee(feesResponse.getUkCopiesFeeResponse());
        PaymentFee overseasCopiesFee = PaymentFee.buildPaymentFee(feesResponse.getOverseasCopiesFeeResponse());
        paymentFees.add(applicationFee);
        paymentFees.add(ukCopiesFee);
        paymentFees.add(overseasCopiesFee);

        return paymentFees;
    }
}
