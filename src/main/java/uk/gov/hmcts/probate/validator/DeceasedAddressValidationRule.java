package uk.gov.hmcts.probate.validator;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class DeceasedAddressValidationRule implements SolExecutorDetailsValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        SolsAddress address = ccdData.getDeceased().getAddress();
        if (address == null || Strings.isNullOrEmpty(address.getAddressLine1())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "deceasedAddressIsNull"));
        }
        if (address == null || Strings.isNullOrEmpty(address.getPostCode())) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "deceasedPostcodeIsNull"));
        }

        return new ArrayList<>(errors);
    }
}
