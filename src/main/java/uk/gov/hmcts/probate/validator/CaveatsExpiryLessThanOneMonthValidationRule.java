package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
class CaveatsExpiryLessThanOneMonthValidationRule implements CaveatsExpiryValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CaveatData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (ccdData.getExpiryDate().isAfter(LocalDate.now().plusMonths(1))) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "caveatExpiryMoreThan1MonthAway"));
        }
        return new ArrayList<>(errors);
    }
}
