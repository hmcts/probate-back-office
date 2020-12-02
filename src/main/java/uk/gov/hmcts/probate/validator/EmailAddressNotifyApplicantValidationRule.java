package uk.gov.hmcts.probate.validator;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
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
public class EmailAddressNotifyApplicantValidationRule implements EmailAddressNotifyValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (ccdData.getApplicationType().equalsIgnoreCase(String.valueOf(PERSONAL))
                && StringUtils.isEmpty(ccdData.getPrimaryApplicantEmailAddress())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailPA"));
        } else if (ccdData.getApplicationType().equalsIgnoreCase(String.valueOf(SOLICITOR)) && StringUtils.isEmpty(ccdData.getSolsSolicitorEmail())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailSOLS"));
        }
        return new ArrayList<>(errors);
    }
}
