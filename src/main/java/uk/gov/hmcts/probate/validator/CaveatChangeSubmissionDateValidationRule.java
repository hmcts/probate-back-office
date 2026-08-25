package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CaveatChangeSubmissionDateValidationRule {

    public static final String CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE = "applicationSubmittedDateIsInTheFuture";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE_WELSH =
            "applicationSubmittedDateIsInTheFutureWelsh";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD = "applicationSubmittedDateBeforeDod";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD_WELSH =
            "applicationSubmittedDateBeforeDodWelsh";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID =
            "applicationSubmittedDateDodMissingOrInvalid";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID_WELSH =
            "applicationSubmittedDateDodMissingOrInvalidWelsh";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING = "applicationSubmittedDateIsEmpty";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_MISSING_WELSH = "applicationSubmittedDateIsEmptyWelsh";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public List<String> validate(CaveatDetails caseDetails) {
        List<String> errors = new ArrayList<>();
        CaveatData caveatData = caseDetails.getData();

        LocalDate applicationSubmittedDate = caveatData.getApplicationSubmittedDate();
        if (applicationSubmittedDate == null) {
            addMessagePair(errors, CODE_APPLICATION_SUBMITTED_DATE_MISSING,
                    CODE_APPLICATION_SUBMITTED_DATE_MISSING_WELSH);
            return errors;
        }

        if (applicationSubmittedDate.isAfter(LocalDate.now())) {
            addMessagePair(errors, CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE,
                    CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE_WELSH);
        }

        LocalDate dod = caveatData.getDeceasedDateOfDeath();
        if (dod == null || dod.isAfter(LocalDate.now())) {
            addMessagePair(errors, CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID,
                    CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID_WELSH);
            return errors;
        }

        if (dod.isAfter(applicationSubmittedDate)) {
            addMessagePair(errors, CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD,
                    CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD_WELSH);
        }

        return errors;
    }

    private void addMessagePair(List<String> errors, String englishCode, String welshCode) {
        errors.add(businessValidationMessageRetriever.getMessage(englishCode, null, Locale.UK));
        errors.add(businessValidationMessageRetriever.getMessage(welshCode, null, Locale.UK));
    }
}
