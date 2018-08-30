package uk.gov.hmcts.probate.validator;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class ExecutorsAddressPostcodeValidationRule implements SolExecutorDetailsValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        ccdData.getExecutors().stream()
                .filter(Executor::isApplying)
                .map(Executor::getAddress)
                .forEach(address -> {
                    if (address == null || Strings.isNullOrEmpty(address.getPostCode())) {
                        errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "executorPostcodeIsNull"));
                    }
                });

        return new ArrayList<>(errors);
    }
}
