package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class EmailAddressNotificationDefaultValidationRule implements EmailAddressNotificationValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (ccdData.getApplicationType().equalsIgnoreCase(String.valueOf(PERSONAL))
            && (!isValid(ccdData.getPrimaryApplicantEmailAddress()))) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "emailNotProvidedPA"));
        } else if (ccdData.getApplicationType().equalsIgnoreCase(String.valueOf(SOLICITOR))
            && (!isValid(ccdData.getPrimaryApplicantEmailAddress()))) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "emailNotProvidedSOLS"));
        }
        return new ArrayList<>(errors);
    }

    private boolean isValid(String email) {
        return !StringUtils.isEmpty(email) && EmailValidator.getInstance().isValid(email);
    }
}
