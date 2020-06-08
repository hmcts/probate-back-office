package uk.gov.hmcts.probate.service.ocr;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;

public class OcrEmailValidator {

    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS = "primaryApplicantEmailAddress";
    private static final String CAVEATOR_EMAIL_ADDRESS = "caveatorEmailAddress";
    private static final String SOLS_SOLICITOR_EMAIL = "solsSolicitorEmail";
    private static final List<String> emailFields = asList(PRIMARY_APPLICANT_EMAIL_ADDRESS, CAVEATOR_EMAIL_ADDRESS, SOLS_SOLICITOR_EMAIL);


    private OcrEmailValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> validateField(List<OCRField> ocrFields) {
        return ocrFields
                .stream()
                .filter(f -> emailFields.contains(f.getName()))
                .filter(f -> isNotValid(f.getValue()))
                .map(f -> format("%s %s does not appear to be a valid email address", f.getName(), f.getValue()))
                .collect(toList());
    }

    private static boolean isNotValid(final String email) {
        return !EmailValidator.getInstance().isValid(email);
    }
}
