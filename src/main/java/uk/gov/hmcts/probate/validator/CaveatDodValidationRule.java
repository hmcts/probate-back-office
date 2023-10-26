package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;

import java.util.Locale;


@Component
@RequiredArgsConstructor
public class CaveatDodValidationRule {

    public static final String CODE_DOD_IN_FUTURE = "dodIsInTheFuture";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaveatDetails caseDetails) {
        CaveatData caveatData = caseDetails.getData();
        LocalDate dod = caveatData.getDeceasedDateOfDeath();
        LocalDate now = LocalDate.now();
        if (dod.isAfter(now)) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage(CODE_DOD_IN_FUTURE, null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Date of death cannot be in the future");
        }
    }

}
