package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;

import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class ApplicationSubmittedDateValidationRule implements CaseworkerAmendAndCreateValidationRule {

    public static final String CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE = "applicationSubmittedDateIsInTheFuture";
    public static final String CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD = "applicationSubmittedDateBeforeDod";

    private final BusinessValidationMessageService businessValidationMessageService;

    @Override
    public List<FieldErrorResponse> validate(CCDData ccdData) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);
        Set<FieldErrorResponse> errors = new HashSet<>();
        LocalDate dod = ccdData.getDeceasedDateOfDeath();
        LocalDate applicationSubmittedDate = LocalDate.parse(ccdData.getApplicationSubmissionDate(), dateTimeFormatter);

        if (applicationSubmittedDate.isAfter(LocalDate.now())) {
            errors.add(businessValidationMessageService.generateError(
                    BUSINESS_ERROR, CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE));
        }
        if (dod.isAfter(applicationSubmittedDate)) {
            errors.add(businessValidationMessageService.generateError(
                    BUSINESS_ERROR, CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD));
        }

        return new ArrayList<>(errors);
    }

}
