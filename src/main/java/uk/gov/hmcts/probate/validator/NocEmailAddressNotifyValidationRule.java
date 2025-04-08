package uk.gov.hmcts.probate.validator;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class NocEmailAddressNotifyValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(ApplicationType applicationType, String solicitorEmail) {
        Set<FieldErrorResponse> errors = new LinkedHashSet<>();

        if (SOLICITOR.equals(applicationType) && StringUtils
                .isEmpty(solicitorEmail)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "notifyApplicantNoEmailSOLS"));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "notifyApplicantNoEmailSOLSWelsh"));
        }
        return new ArrayList<>(errors);
    }
}
