package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import java.util.Locale;
import static uk.gov.hmcts.probate.model.Constants.EMAIL_VALIDATION_REGEX;

@Component
@RequiredArgsConstructor
public class CaveatorEmailAddressValidationRule implements CaveatEmailValidationRule{
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String EMAIL_NOT_FOUND_CAVEAT = "emailNotProvidedCaveats";

    @Override
    public void validate(CaveatDetails caveatDetails) {
        String[] args = {caveatDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(EMAIL_NOT_FOUND_CAVEAT, args, Locale.UK);

        if (caveatDetails.getData().getCaveatorEmailAddress() != null && !caveatDetails.getData().getCaveatorEmailAddress().matches(EMAIL_VALIDATION_REGEX)) {
            throw new BusinessValidationException(userMessage,
                    "Caveator email does not meet the criteria for case id " + caveatDetails.getId());
        }
    }
}
