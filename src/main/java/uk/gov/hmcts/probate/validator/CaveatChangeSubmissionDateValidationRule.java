package uk.gov.hmcts.probate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.payments.PaymentsService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@Slf4j
public class CaveatChangeSubmissionDateValidationRule {
    private static final ZoneId LONDON_ZONE_ID = ZoneId.of("Europe/London");

    public static final String CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE = "applicationSubmittedDateIsInTheFuture";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD = "applicationSubmittedDateBeforeDod";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID =
            "applicationSubmittedDateDodMissingOrInvalid";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING = "applicationSubmittedDateIsEmpty";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING_PAYMENT =
            "applicationSubmittedDateMissingPayment";

    private final PaymentsService paymentsService;
    private final BusinessValidationMessageService businessValidationMessageService;
    private final Clock clock;

    public CaveatChangeSubmissionDateValidationRule(PaymentsService paymentsService,
                                                    BusinessValidationMessageService businessValidationMessageService,
                                                    Clock clock) {
        this.paymentsService = paymentsService;
        this.businessValidationMessageService = businessValidationMessageService;
        this.clock = clock.withZone(LONDON_ZONE_ID);
    }

    public List<FieldErrorResponse> validate(CaveatDetails caseDetails) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        CaveatData caveatData = caseDetails.getData();
        LocalDate today = LocalDate.now(clock);

        LocalDate applicationSubmittedDate = caveatData.getApplicationSubmittedDate();
        if (applicationSubmittedDate == null) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_MISSING));
            return errors;
        }

        if (applicationSubmittedDate.isAfter(today)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE));
        }

        LocalDate dod = caveatData.getDeceasedDateOfDeath();
        if (dod == null || dod.isAfter(today)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID));
            return errors;
        }

        if (dod.isAfter(applicationSubmittedDate)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD));
        }

        // Accept paymentTaken=Yes for manual/legacy paid cases with no linked payments-platform record,
        // otherwise genuine paid caveat cases could be blocked from submission-date correction.
        boolean hasSuccessfulPayment = paymentsService.hasSuccessfulPaymentByCaseId(caseDetails.getId().toString());
        boolean isPaymentTaken = YES.equalsIgnoreCase(caveatData.getPaymentTaken());
        if (!(hasSuccessfulPayment || isPaymentTaken)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    CODE_APPLICATION_SUBMITTED_DATE_MISSING_PAYMENT));
        }

        return errors;
    }

}
