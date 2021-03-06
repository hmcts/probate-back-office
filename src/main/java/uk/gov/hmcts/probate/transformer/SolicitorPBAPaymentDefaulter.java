package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.fee.FeeService;

import java.math.BigDecimal;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RequiredArgsConstructor
@Service
public class SolicitorPBAPaymentDefaulter {
    private final FeeService feeService;

    public void defaultPageFlowForPayments(CaseData data,
                                           ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        BigDecimal amountInPounds = InheritanceTax.builder().netValue(data.getIhtNetValue())
            .build().getNetValueInPounds();

        FeesResponse feesResponse = feeService.getAllFeesData(
            amountInPounds,
            data.getExtraCopiesOfGrant(),
            data.getOutsideUKGrantCopies());

        responseCaseDataBuilder.solsNeedsPBAPayment(feesResponse.getTotalAmount().doubleValue() == 0
            ? NO : YES);
    }
}
