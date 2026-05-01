package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import static uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer.dateTimeFormatter;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@RequiredArgsConstructor
public class IntestacyDivorceOrSeparationValidationRule implements ValidationRule {
    public static final String DIVORCED_OUTSIDE_ENGLAND_OR_WALES = "divorcedOutsideEnglandOrWales";
    public static final String DIVORCED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "divorcedOutsideEnglandOrWalesWelsh";
    public static final String SEPARATED_OUTSIDE_ENGLAND_OR_WALES = "separatedOutsideEnglandOrWales";
    public static final String SEPARATED_OUTSIDE_ENGLAND_OR_WALES_WELSH = "separatedOutsideEnglandOrWalesWelsh";
    public static final String INVALID_DIVORCE_OR_SEPARATION_DATE = "invalidDivorceOrSeparationDate";
    public static final String INVALID_DIVORCE_OR_SEPARATION_DATE_WELSH = "invalidDivorceOrSeparationDateWelsh";

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CCDData ccdData) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        var deceased = ccdData.getDeceased();
        if (deceased != null) {
            List<String> codes = new ArrayList<>();
            String deceasedMaritalStatus = deceased.getDeceasedMaritalStatus();
            String deceasedDivorcedInEnglandOrWales = deceased.getDeceasedDivorcedInEnglandOrWales();
            String divorceOrSeparationDate = deceased.getDateOfDivorcedCPJudicially();
            LocalDate dob = deceased.getDateOfBirth();
            LocalDate dod = deceased.getDateOfDeath();

            if (DIVORCED_VALUE.equals(deceasedMaritalStatus)
                    && NO.equalsIgnoreCase(deceasedDivorcedInEnglandOrWales)) {
                codes.add(DIVORCED_OUTSIDE_ENGLAND_OR_WALES);
                codes.add(DIVORCED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
            } else if (JUDICIALLY_SEPARATED_VALUE.equals(deceasedMaritalStatus)
                    && NO.equalsIgnoreCase(deceasedDivorcedInEnglandOrWales)) {
                codes.add(SEPARATED_OUTSIDE_ENGLAND_OR_WALES);
                codes.add(SEPARATED_OUTSIDE_ENGLAND_OR_WALES_WELSH);
            }

            if (null != divorceOrSeparationDate) {
                LocalDate divorceOrSeparationLocalDate = LocalDate.parse(divorceOrSeparationDate, dateTimeFormatter);

                boolean invalid = (DIVORCED_VALUE.equals(deceasedMaritalStatus)
                        || JUDICIALLY_SEPARATED_VALUE.equals(deceasedMaritalStatus))
                        && (divorceOrSeparationLocalDate.isAfter(dod)
                        || !divorceOrSeparationLocalDate.isAfter(dob)
                        || divorceOrSeparationLocalDate.isAfter(LocalDate.now()));

                if (invalid) {
                    codes.add(INVALID_DIVORCE_OR_SEPARATION_DATE);
                    codes.add(INVALID_DIVORCE_OR_SEPARATION_DATE_WELSH);
                }
            }

            codes.forEach(code -> errors.add(businessValidationMessageService
                    .generateError(BUSINESS_ERROR, code)));
        }
        return errors;
    }
}
