package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentsResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaseServiceRequestValidationRule {

    public static final String SERVICE_REQUEST_EXISTS = "ServiceRequestExists";
    public static final String SERVICE_REQUEST_EXISTS_WELSH = "ServiceRequestExistsWelsh";


    private final PaymentsService paymentsService;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        validateByCaseId(caseDetails.getId());
    }

    public void validate(CaveatDetails caveatDetails) {
        validateByCaseId(caveatDetails.getId());
    }

    private void validateByCaseId(Long caseId) {
        if (!hasServiceRequest(caseId)) {
            return;
        }

        String userMessage = businessValidationMessageRetriever
                .getMessage(SERVICE_REQUEST_EXISTS, null, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever
                .getMessage(SERVICE_REQUEST_EXISTS_WELSH, null, Locale.UK);
        throw new BusinessValidationException(userMessage,
                "Service request already exists for case: " + caseId, userMessageWelsh);
    }

    private boolean hasServiceRequest(Long caseId) {
        PaymentsResponse payments = paymentsService.retrievePayments(String.valueOf(caseId));
        if (payments == null) {
            log.info("Payments response is null, caseId: {}", caseId);
            return false;
        }

        log.info("Payments: {} , caseId:{} ", payments, caseId);
        return payments.getPayments() != null && !payments.getPayments().isEmpty();
    }
}
