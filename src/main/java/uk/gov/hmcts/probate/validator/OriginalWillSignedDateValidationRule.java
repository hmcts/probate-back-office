package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class OriginalWillSignedDateValidationRule implements ValidationRule {
    private static final String ORIGINAL_WILL_SIGNED_DATE_MUST_BE_IN_THE_PAST = "originalWillSignedDateMustBeInThePast";
    private static final String ORIGINAL_WILL_SIGNED_DATE_MUST_BE_BEFORE_DATE_OF_DEATH
            = "originalWillSignedDateMustBeBeforeDateOfDeath";

    private final BusinessValidationMessageService businessValidationMessageService;
    
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        return Optional.ofNullable(ccdData)
                .map(c -> getErrorCodeForOriginalWillDateDateNotInThePast(
                        c.getOriginalWillSignedDate(), c.getDeceasedDateOfDeath()))
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }

    private List<String> getErrorCodeForOriginalWillDateDateNotInThePast(
            LocalDate willSignedDate, LocalDate dateOfDeath) {
        List<String> allErrorCodes = new ArrayList<>();
        if (willSignedDate == null || dateOfDeath == null) {
            return allErrorCodes;
        }
        if (!willSignedDate.isBefore(now())) {
            allErrorCodes.add(ORIGINAL_WILL_SIGNED_DATE_MUST_BE_IN_THE_PAST);
        }
        if (willSignedDate.isAfter(dateOfDeath)) {
            allErrorCodes.add(ORIGINAL_WILL_SIGNED_DATE_MUST_BE_BEFORE_DATE_OF_DEATH);
        }
        return allErrorCodes;
    }
}
