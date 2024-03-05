package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.List;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class CaveatAcknowledgementValidationRule {

    public static final String PAYMENT_ACKNOWLEDGEMENT = "paymentAcknowledgementError";
    public static final String PAYMENT_CONFIRMATION = "paymentAcknowledgement";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaveatDetails caseDetails) {
        CaveatData caveatData = caseDetails.getData();
        List<String> paymentConfirmation = caveatData.getPaymentConfirmCheckbox();
        String confirmation = (null != paymentConfirmation && !paymentConfirmation.isEmpty())
                ? caveatData.getPaymentConfirmCheckbox().get(0) : "";
        log.info("Payment confirmation: {}", confirmation);
        if (!PAYMENT_CONFIRMATION.equals(confirmation)) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage(PAYMENT_ACKNOWLEDGEMENT, null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "You must confirm that you understand that you must complete payment after you’ve submitted your "
                            + "application");
        }
    }

}
