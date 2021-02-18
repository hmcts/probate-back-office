package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditAccountPaymentValidationRule {

    public static final String CREDIT_ACCOUNT_PAYMENT_SUCCESS = "Success";
    public static final String CREDIT_ACCOUNT_PAYMENT_ERROR_RESOURCE = "creditAccountPaymentError";
    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(String selectedPBA, String caseId, PaymentResponse paymentResponse) {
        Set<FieldErrorResponse> errors = new HashSet<>();
        if (!CREDIT_ACCOUNT_PAYMENT_SUCCESS.equalsIgnoreCase(paymentResponse.getStatus())) {
            String[] args = {caseId, selectedPBA, paymentResponse.getStatus()};
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                CREDIT_ACCOUNT_PAYMENT_ERROR_RESOURCE, args));
        }

        return new ArrayList<>(errors);
    }

}
