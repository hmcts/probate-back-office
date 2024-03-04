package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CaveatAcknowledgementValidationRule {

    public static final String PAYMENT_ACKNOWLEDGEMENT = "paymentAcknowledgementError";
    public static final String PAYMENT_CONFIRMATION = "paymentAcknowledgement";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaveatDetails caseDetails) {
        CaveatData caveatData = caseDetails.getData();
        String paymentConfirmation = caveatData.getPaymentConfirmCheckbox();
        if (!PAYMENT_CONFIRMATION.equals(paymentConfirmation)) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage(PAYMENT_ACKNOWLEDGEMENT, null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "You must confirm that you understand that you must complete payment after you’ve submitted your "
                            + "application");
        }
    }

}
