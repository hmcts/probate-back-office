package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;

@Component
@RequiredArgsConstructor
public class IntestacyDivorceOrSeparationDateValidationRule implements CaseworkerAmendAndCreateValidationRule {
    public static final String INVALID_DIVORCE_OR_SEPARATION_DATE = "invalidDivorceOrSeparationDate";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);
        List<FieldErrorResponse> errors = new ArrayList<>();
        var deceased = ccdData.getDeceased();
        if (deceased != null) {
            String deceasedMaritalStatus = deceased.getDeceasedMaritalStatus();
            String divorceOrSeparationDate = deceased.getDateOfDivorcedCPJudicially();
            LocalDate dob = deceased.getDateOfBirth();
            LocalDate dod = deceased.getDateOfDeath();

            if (null != ccdData.getApplicationSubmissionDate() && null != divorceOrSeparationDate) {
                LocalDate applicationSubmittedDate = LocalDate.parse(
                        ccdData.getApplicationSubmissionDate(), dateTimeFormatter);
                LocalDate divorceOrSeparationLocalDate = LocalDate.parse(divorceOrSeparationDate, dateTimeFormatter);

                boolean invalid = (DIVORCED_VALUE.equals(deceasedMaritalStatus)
                        || JUDICIALLY_SEPARATED_VALUE.equals(deceasedMaritalStatus))
                        && (divorceOrSeparationLocalDate.isAfter(dod)
                        || !divorceOrSeparationLocalDate.isAfter(dob)
                        || divorceOrSeparationLocalDate.isAfter(applicationSubmittedDate));

                if (invalid) {
                    errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                            INVALID_DIVORCE_OR_SEPARATION_DATE));
                }
            }
        }
        return errors;
    }
}
