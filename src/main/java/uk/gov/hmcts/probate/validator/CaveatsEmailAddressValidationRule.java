package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.EmailValidationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class CaveatsEmailAddressValidationRule implements CaveatsEmailAddressNotificationValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;
    private final EmailValidationService emailValidationService = new EmailValidationService();

    @Override
    public List<FieldErrorResponse> validate(CaveatData caveatData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (caveatData.getCaveatorEmailAddress().isEmpty() || caveatData.getCaveatorEmailAddress() == null) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "emailNotProvidedCaveats"));
        }
        if (errors.isEmpty() && !emailValidationService.validateEmailAddress(caveatData)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "emailInvalidCaveats"));
        }
        return new ArrayList<>(errors);
    }
}
