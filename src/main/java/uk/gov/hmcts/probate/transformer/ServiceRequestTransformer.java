package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;
import uk.gov.hmcts.probate.model.payments.servicerequest.CasePayentRequestDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestDto;
import uk.gov.hmcts.probate.service.payments.PaymentFeeBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ServiceRequestTransformer {
    @Autowired
    private PaymentFeeBuilder paymentFeeBuilder;

    //http://civil-general-applications-demo.service.core-compute-demo.internal/payment-request-update
    private static final String GRANT_OF_REPRESENTATION_CALLBACK = "/payment/gor-payment-request-update";
    private static final String CAVEAT_CALLBACK = "/payment/caveat-payment-request-update";
    private static final String ACTION = "payment attempt created";

    public ServiceRequestDto buildServiceRequest(CaseDetails caseDetails, FeesResponse feesResponse) {
        String party = caseDetails.getData().getSolsSOTForenames() + " "
                + caseDetails.getData().getSolsSOTSurname();
        CasePayentRequestDto casePayentRequestDto = CasePayentRequestDto.builder()
                .responsibleParty(party).action(ACTION).build();
        List<PaymentFee> fees = buildFees(caseDetails.getData(), feesResponse);
        return ServiceRequestDto.builder()
                .callbackUrl(GRANT_OF_REPRESENTATION_CALLBACK)
                .casePaymentRequest(casePayentRequestDto)
                .caseReference(caseDetails.getData().getSolsPBAPaymentReference())
                .ccdCaseNumber(caseDetails.getId().toString())
                .fees(fees)
                .build();

    }

    public ServiceRequestDto buildServiceRequest(CaveatDetails caseDetails, FeeResponse feeResponse) {
        String party = caseDetails.getData().getCaveatorForenames() + " "
                + caseDetails.getData().getCaveatorSurname();
        CasePayentRequestDto casePayentRequestDto = CasePayentRequestDto.builder()
                .responsibleParty(party).action(ACTION).build();

        PaymentFee paymentFee = paymentFeeBuilder.buildPaymentFee(feeResponse, BigDecimal.ONE);
        List<PaymentFee> fees = new ArrayList<>();
        fees.add(paymentFee);
        return ServiceRequestDto.builder()
                .callbackUrl(CAVEAT_CALLBACK)
                .casePaymentRequest(casePayentRequestDto)
                .caseReference(caseDetails.getData().getSolsPBAPaymentReference())
                .ccdCaseNumber(caseDetails.getId().toString())
                .fees(fees)
                .build();

    }

    private List<PaymentFee> buildFees(CaseData caseData, FeesResponse feesResponse) {
        return buildFees(feesResponse, caseData.getExtraCopiesOfGrant(), caseData.getOutsideUKGrantCopies());
    }

    private List<PaymentFee> buildFees(FeesResponse feesResponse, Long extraCopies,
                                       Long outseideUKGrantCopies) {
        ArrayList<PaymentFee> paymentFees = new ArrayList<>();
        PaymentFee applicationFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getApplicationFeeResponse(),
                BigDecimal.ONE);
        paymentFees.add(applicationFee);

        if (extraCopies != null && extraCopies > 0) {
            PaymentFee ukCopiesFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getUkCopiesFeeResponse(),
                    BigDecimal.valueOf(extraCopies));
            paymentFees.add(ukCopiesFee);
        }
        if (outseideUKGrantCopies != null && outseideUKGrantCopies > 0) {
            PaymentFee overseasCopiesFee =
                    paymentFeeBuilder.buildPaymentFee(feesResponse.getOverseasCopiesFeeResponse(),
                            BigDecimal.valueOf(outseideUKGrantCopies));
            paymentFees.add(overseasCopiesFee);
        }

        return paymentFees;
    }

}
