package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;

import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class ApplicationSubmittedDateValidationRule implements CaseworkerAmendAndCreateValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        Set<FieldErrorResponse> errors = new HashSet<>();
        LocalDate dod = ccdData.getDeceasedDateOfDeath();
        LocalDate applicationSubmittedDate = ccdData.getCaseSubmissionDate();
        try {

            if (applicationSubmittedDate.isAfter(LocalDate.now())) {
                errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "applicationSubmittedDateIsInTheFuture"));
            }
            if (dod.isAfter(applicationSubmittedDate)) {
                errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "dodIsAfterApplicationSubmittedDate"));
            }
        } catch (DateTimeParseException dtpe) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "ApplicationSubmittedDateInvalid"));
        }
        return new ArrayList<>(errors);
    }

}
