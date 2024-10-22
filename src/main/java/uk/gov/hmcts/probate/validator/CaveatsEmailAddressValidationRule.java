package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class CaveatsEmailAddressValidationRule implements CaveatsEmailAddressNotificationValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CaveatData ccdData) {
        Set<FieldErrorResponse> errors = new LinkedHashSet<>();

        if (ccdData.getCaveatorEmailAddress().isEmpty()) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "emailNotProvidedCaveats"));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "emailNotProvidedCaveatsWelsh"));
        }
        return new ArrayList<>(errors);
    }
}
