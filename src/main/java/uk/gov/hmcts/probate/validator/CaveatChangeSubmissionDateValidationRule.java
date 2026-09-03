package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class CaveatChangeSubmissionDateValidationRule {

    public static final String CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE = "applicationSubmittedDateIsInTheFuture";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD = "applicationSubmittedDateBeforeDod";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID =
            "applicationSubmittedDateDodMissingOrInvalid";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING = "applicationSubmittedDateIsEmpty";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING_PAYMENT =
            "applicationSubmittedDateMissingPayment";

    private final PaymentsService paymentsService;
    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CaveatDetails caseDetails) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        CaveatData caveatData = caseDetails.getData();

        LocalDate applicationSubmittedDate = caveatData.getApplicationSubmittedDate();
        if (applicationSubmittedDate == null) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_MISSING));
            return errors;
        }

        if (applicationSubmittedDate.isAfter(LocalDate.now())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE));
        }

        LocalDate dod = caveatData.getDeceasedDateOfDeath();
        if (dod == null || dod.isAfter(LocalDate.now())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID));
            return errors;
        }

        if (dod.isAfter(applicationSubmittedDate)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD));
        }

        if (!paymentsService.isPaymentSuccessByCaseId(caseDetails.getId().toString())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_MISSING_PAYMENT));
        }

        return errors;
    }

}
