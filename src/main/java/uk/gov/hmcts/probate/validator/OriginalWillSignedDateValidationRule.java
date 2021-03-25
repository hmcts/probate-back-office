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
                .map(this::getErrorCodeForOriginalWillDateDateNotInThePast)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(code -> businessValidationMessageService.generateError(BUSINESS_ERROR, code))
                .collect(Collectors.toList());
    }

    private List<String> getErrorCodeForOriginalWillDateDateNotInThePast(CCDData ccdData) {
        List<String> allErrorCodes = new ArrayList<>();
        LocalDate willSignedDate = ccdData.getOriginalWillSignedDate();
        if (willSignedDate != null) {
            if (willSignedDate.compareTo(LocalDate.now()) >= 0) {
                allErrorCodes.add(ORIGINAL_WILL_SIGNED_DATE_MUST_BE_IN_THE_PAST);
            }
            LocalDate dateOfDeath = ccdData.getDeceasedDateOfDeath();
            if (dateOfDeath != null && willSignedDate.compareTo(dateOfDeath) >= 0) {
                allErrorCodes.add(ORIGINAL_WILL_SIGNED_DATE_MUST_BE_BEFORE_DATE_OF_DEATH);
            }
        }

        return allErrorCodes;
    }
}
