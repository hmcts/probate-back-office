package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPaperPaymentMethod;

@Slf4j
@Component
public class OCRFieldPaymentMethodMapper {

    private static final String PAYMENT_METHOD_DEBTORCREDIT = "debitOrCredit";
    private static final String PAYMENT_METHOD_CHEQUE = "cheque";
    private static final String PAYMENT_METHOD_CASH = "cash";
    private static final String PAYMENT_METHOD_FEEACCOUNT = "feeAccount";

    @ToPaperPaymentMethod
    public String validateKnownPaymentMethod(String paymentMethod) {
        log.info("Beginning mapping for Paper Payment Method value: {}", paymentMethod);

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return null;
        } else {
            switch (paymentMethod.toUpperCase().trim()) {
                case "DEBITORCREDIT":
                    return PAYMENT_METHOD_DEBTORCREDIT;
                case "CHEQUE":
                    return PAYMENT_METHOD_CHEQUE;
                case "CASH":
                    return PAYMENT_METHOD_CASH;
                case "FEEACCOUNT":
                    return PAYMENT_METHOD_FEEACCOUNT;
                default:
                    String errorMessage = "Payment method debitOrCredit, cheque, feeAccount or cash expected but got '"
                            + paymentMethod + "'";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
            }
        }
    }

}
