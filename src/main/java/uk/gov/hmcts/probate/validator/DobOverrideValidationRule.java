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
public class DobOverrideValidationRule implements CaseDetailsValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Override
    public void validate(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getData();
        String newDobValue = caseData.getDobOverride();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);
        try {
            LocalDate.parse(newDobValue, dateTimeFormatter);
        } catch (DateTimeParseException dtpe) {
            String userMessage = businessValidationMessageRetriever.getMessage("dobOverrideDateInvalid", null,
                    Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "dob override date is not valid for case: " + caseDetails.getId());
        }
    }
}
