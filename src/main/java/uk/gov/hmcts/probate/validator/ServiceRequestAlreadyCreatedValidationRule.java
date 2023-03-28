package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ServiceRequestAlreadyCreatedValidationRule {

    private static final String SERVICE_REQUEST_ALREADY_CREATED = "serviceRequestAlreadyCreated";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        if (!StringUtils.isEmpty(caseDetails.getData().getServiceRequestReference())
                && anyPaymentsAlreadySuccessful(caseDetails.getData().getPayments())) {
            throwError(caseDetails.getId().toString());
        }
    }

    public void validate(CaveatDetails caseDetails) {
        if (!StringUtils.isEmpty(caseDetails.getData().getServiceRequestReference())
                && anyPaymentsAlreadySuccessful(caseDetails.getData().getPayments())) {
            throwError(caseDetails.getId().toString());
        }
    }

    private boolean anyPaymentsAlreadySuccessful(List<CollectionMember<Payment>> payments) {
        if (payments == null) {
            return true;
        }
        for (CollectionMember<Payment> paymentCollectionMember : payments) {
            Payment payment = paymentCollectionMember.getValue();
            if (PaymentStatus.SUCCESS.getName().equals(payment.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private void throwError(String caseId) {
        String[] args = {caseId};
        String userMessage = businessValidationMessageRetriever
                .getMessage(SERVICE_REQUEST_ALREADY_CREATED, args, Locale.UK);
        throw new BusinessValidationException(userMessage,
                "Service request for payment already created for case:" + caseId);
    }

}
