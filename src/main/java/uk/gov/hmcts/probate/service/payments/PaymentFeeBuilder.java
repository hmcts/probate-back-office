package uk.gov.hmcts.probate.service.payments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.model.fee.FeeResponse;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFeeBuilder {
    private final FeeServiceConfiguration feeServiceConfiguration;

    public PaymentFee buildPaymentFee(FeeResponse feeResponse, BigDecimal volume) {
        return PaymentFee.builder()
            .calculatedAmount(feeResponse.getFeeAmount().setScale(2))
            .code(feeResponse.getCode())
            .description(feeResponse.getDescription())
            .feeAmount(feeResponse.getFeeAmount().setScale(2))
            .jurisdiction1(feeServiceConfiguration.getJurisdiction1())
            .jurisdiction2(feeServiceConfiguration.getJurisdiction2())
            .version(feeResponse.getVersion())
            .volume(volume)
            .build();
    }

}
