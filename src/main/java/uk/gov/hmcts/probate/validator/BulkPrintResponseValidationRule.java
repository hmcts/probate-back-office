package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class BulkPrintResponseValidationRule implements BulkPrintValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (ccdData.getSendLetterId().equalsIgnoreCase(null) || ccdData.getSendLetterId().isEmpty()) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "bulkPrintResponseNull"));
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "bulkPrintResponseNullWelsh"));
        }
        return new ArrayList<>(errors);
    }
}
