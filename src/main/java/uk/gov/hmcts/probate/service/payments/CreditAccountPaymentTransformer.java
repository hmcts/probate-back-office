package uk.gov.hmcts.probate.service.payments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreditAccountPaymentTransformer {
    private static final String PROBATE_SOLICITOR_PAYMENT_DESCRIPTION = "Probate Solicitor payment";

    @Autowired
    private PaymentFeeBuilder paymentFeeBuilder;

    @Value("${fee.api.service}")
    private String service;

    @Value("${payment.currency}")
    private String currency;

    @Value("${payment.siteId}")
    private String siteId;

    public CreditAccountPayment transform(CaseDetails caseDetails, FeesResponse feesResponse) {
        List<PaymentFee> paymentFees = buildFees(caseDetails.getData(), feesResponse);

        return CreditAccountPayment.builder()
            .accountNumber(caseDetails.getData().getSolsPBANumber().getValue().getCode())
            .fees(paymentFees)
            .caseReference(caseDetails.getData().getSolsSolicitorAppReference())
            .amount(feesResponse.getTotalAmount())
            .ccdCaseNumber(caseDetails.getId().toString())
            .currency(currency)
            .description(PROBATE_SOLICITOR_PAYMENT_DESCRIPTION)
            .organisationName(caseDetails.getData().getSolsSolicitorFirmName())
            .service(service)
            .siteId(siteId)
            .customerReference(caseDetails.getData().getSolsSolicitorAppReference())
            .build();
    }


    private List<PaymentFee> buildFees(CaseData caseData, FeesResponse feesResponse) {
        ArrayList<PaymentFee> paymentFees = new ArrayList<>();
        PaymentFee applicationFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getApplicationFeeResponse(), BigDecimal.ONE);
        paymentFees.add(applicationFee);

        if (caseData.getExtraCopiesOfGrant() > 0) {
            PaymentFee ukCopiesFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getUkCopiesFeeResponse(),
                BigDecimal.valueOf(caseData.getExtraCopiesOfGrant()));
            paymentFees.add(ukCopiesFee);
        }
        if (caseData.getOutsideUKGrantCopies() > 0) {
            PaymentFee overseasCopiesFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getOverseasCopiesFeeResponse(),
                BigDecimal.valueOf(caseData.getOutsideUKGrantCopies()));
            paymentFees.add(overseasCopiesFee);
        }

        return paymentFees;
    }
}
