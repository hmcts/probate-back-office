package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SolicitorPaymentMethodValidationRule implements CaseDetailsValidationRule, CaveatDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String SOLS_PAY_METHOD_NOT_FEE_ACCOUNT = "solsPayMethodNotFeeAccount";
    private static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";

    @Override
    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();

        String payMethod = caseData.getSolsPaymentMethods();
        ApplicationType applicationType = caseData.getApplicationType();
        String caseId  = caseDetails.getId().toString();
        validatePaymentMethod(payMethod, applicationType, caseId);
    }

    @Override
    public void validate(CaveatDetails caveatDetails) {
        CaveatData caveatData = caveatDetails.getData();
        String payMethod = caveatData.getSolsPaymentMethods();
        ApplicationType applicationType = caveatData.getApplicationType();
        String caseId  = caveatDetails.getId().toString();
        validatePaymentMethod(payMethod, applicationType, caseId);
    }

    private void validatePaymentMethod(String payMethod, ApplicationType applicationType, String caseId) {
        if (applicationType == ApplicationType.SOLICITOR) {
            if (!PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(payMethod)) {
                String[] args = {caseId};
                String userMessage = businessValidationMessageRetriever.getMessage(SOLS_PAY_METHOD_NOT_FEE_ACCOUNT, 
                    args, Locale.UK);
                throw new BusinessValidationException(userMessage,
                    "Fee Account payment method has not been selected: " + caseId);
            }
        }
    }
}
