package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SolicitorPaymentMethodValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static final String ADDRESS_NOT_FOUND = "solsPayMethodNotFeeAccount";
    private static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";

    @Override
    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();

        if (caseData.getApplicationType() == ApplicationType.SOLICITOR) {
            if (!PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(caseData.getSolsPaymentMethods())) {
                String[] args = {caseDetails.getId().toString()};
                String userMessage = businessValidationMessageRetriever.getMessage(ADDRESS_NOT_FOUND, args, Locale.UK);
                throw new BusinessValidationException(userMessage,
                    "Fee Account payment method has not been selected: "
                        + caseDetails.getId());
            }
        }
    }
}
