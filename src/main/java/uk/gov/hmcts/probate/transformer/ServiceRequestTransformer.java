package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;
import uk.gov.hmcts.probate.model.payments.servicerequest.CasePaymentRequestDto;
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
    @Value("${payment.serviceRequest.hmctsOrgId}")
    private String hmctsOrgId;
    @Value("${payment.serviceRequest.GrantOfRepresentationCallbackUrl}")
    private String grantOfRepresentationCallback;
    @Value("${payment.serviceRequest.CaveatCallbackUrl}")
    private String caveatCallback;

    private static final String ACTION = "payment attempt created";

    public ServiceRequestDto buildServiceRequest(CaseDetails caseDetails, FeesResponse feesResponse) {
        String party = caseDetails.getData().getSolsSOTForenames() + " "
                + caseDetails.getData().getSolsSOTSurname();
        CasePaymentRequestDto casePaymentRequestDto = CasePaymentRequestDto.builder()
                .responsibleParty(party).action(ACTION).build();
        List<PaymentFee> fees = buildFees(caseDetails.getData(), feesResponse);
        return ServiceRequestDto.builder()
                .callbackUrl(grantOfRepresentationCallback)
                .casePaymentRequest(casePaymentRequestDto)
                .caseReference(caseDetails.getData().getSolsPBAPaymentReference())
                .ccdCaseNumber(caseDetails.getId().toString())
                .hmctsOrgId(hmctsOrgId)
                .fees(fees)
                .build();

    }

    public ServiceRequestDto buildServiceRequest(CaveatDetails caseDetails, FeeResponse feeResponse) {
        String party = caseDetails.getData().getCaveatorForenames() + " "
                + caseDetails.getData().getCaveatorSurname();
        CasePaymentRequestDto casePaymentRequestDto = CasePaymentRequestDto.builder()
                .responsibleParty(party).action(ACTION).build();

        PaymentFee paymentFee = paymentFeeBuilder.buildPaymentFee(feeResponse, BigDecimal.ONE);
        List<PaymentFee> fees = new ArrayList<>();
        fees.add(paymentFee);
        return ServiceRequestDto.builder()
                .callbackUrl(caveatCallback)
                .casePaymentRequest(casePaymentRequestDto)
                .caseReference(caseDetails.getData().getSolsPBAPaymentReference())
                .ccdCaseNumber(caseDetails.getId().toString())
                .hmctsOrgId(hmctsOrgId)
                .fees(fees)
                .build();

    }

    protected List<PaymentFee> buildFees(CaseData caseData, FeesResponse feesResponse) {
        return buildFees(feesResponse, caseData.getExtraCopiesOfGrant(), caseData.getOutsideUKGrantCopies());
    }

    private List<PaymentFee> buildFees(FeesResponse feesResponse, Long extraCopies,
                                       Long outsideUKGrantCopies) {
        ArrayList<PaymentFee> paymentFees = new ArrayList<>();
        PaymentFee applicationFee = paymentFeeBuilder.buildPaymentFee(feesResponse.getApplicationFeeResponse(),
                BigDecimal.ONE);
        paymentFees.add(applicationFee);
        BigDecimal totalCopiesFee = new BigDecimal(0).setScale(2);
        BigDecimal totalCopies = new BigDecimal(0);
        FeeResponse copiesFee = null;
        if (extraCopies != null && extraCopies > 0) {
            totalCopiesFee = totalCopiesFee.add(feesResponse.getUkCopiesFeeResponse().getFeeAmount());
            totalCopies = totalCopies.add(BigDecimal.valueOf(extraCopies));
            copiesFee = feesResponse.getUkCopiesFeeResponse();
        }
        if (outsideUKGrantCopies != null && outsideUKGrantCopies > 0) {
            totalCopiesFee = totalCopiesFee.add(feesResponse.getOverseasCopiesFeeResponse().getFeeAmount());
            totalCopies = totalCopies.add(BigDecimal.valueOf(outsideUKGrantCopies));
            copiesFee = feesResponse.getOverseasCopiesFeeResponse();
        }
        if (totalCopiesFee.compareTo(BigDecimal.ZERO) > 0 && copiesFee != null) {
            PaymentFee totalCopiesPayment = paymentFeeBuilder.buildCopiesPaymentFee(totalCopiesFee, copiesFee.getCode(),
                                                    copiesFee.getDescription(), copiesFee.getVersion(), totalCopies);
            paymentFees.add(totalCopiesPayment);
        }
        return paymentFees;
    }
}
