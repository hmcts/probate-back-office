package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ApplicationSubmittedDateValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);
        CaseData caseData = caseDetails.getData();
        String newApplicationSubmittedDate = caseData.getApplicationSubmittedDate();
        LocalDate dod = caseData.getDeceasedDateOfDeath();
        LocalDate applicationSubmittedDate = LocalDate.parse(newApplicationSubmittedDate, dateTimeFormatter);
        try {

            if (applicationSubmittedDate.isAfter(LocalDate.now())) {
                String userMessage = businessValidationMessageRetriever.getMessage(
                        "applicationSubmittedDateIsInTheFuture", null, Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "Application Submitted Date cannot be a future date for case: " + caseDetails.getId());
            }
            if (dod.isAfter(applicationSubmittedDate)) {
                String userMessage = businessValidationMessageRetriever.getMessage(
                        "dodIsAfterApplicationSubmittedDate", null, Locale.UK);
                throw new BusinessValidationException(userMessage,
                        "Date of Death cannot be after Application Submitted Date for case: " + caseDetails.getId());
            }
        } catch (DateTimeParseException dtpe) {
            String userMessage = businessValidationMessageRetriever.getMessage("dobOverrideDateInvalid",
                    null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Application Submitted Date is invalid format for case: " + caseDetails.getId());
        }
    }

}
