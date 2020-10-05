package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class OcrEmailValidator {

    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS = "primaryApplicantEmailAddress";
    private static final String CAVEATOR_EMAIL_ADDRESS = "caveatorEmailAddress";
    private static final String SOLS_SOLICITOR_EMAIL = "solsSolicitorEmail";

    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION = "Primary applicant email address";
    private static final String CAVEATOR_EMAIL_ADDRESS_DESCRIPTION = "Caveator email address";
    private static final String SOLS_SOLICITOR_EMAIL_DESCRIPTION = "Solicitor email address";

    private static Map<String, String> emailFields = new HashMap<>();

    static {
        emailFields.put(PRIMARY_APPLICANT_EMAIL_ADDRESS, PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(CAVEATOR_EMAIL_ADDRESS, CAVEATOR_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(SOLS_SOLICITOR_EMAIL, SOLS_SOLICITOR_EMAIL_DESCRIPTION);
    }

    protected static final String S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS = "%s (%s) does not appear to be a valid email address";

    public List<String> validateField(List<OCRField> ocrFields) {
        return ocrFields
                .stream()
                .filter(f -> emailFields.containsKey(f.getName()))
                .filter(f -> isNotValid(f.getValue()))
                .map(f -> format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, emailFields.get(f.getName()), f.getName()))
                .collect(toList());
    }

    public List<String> validateNonEmptyField(List<OCRField> ocrFields) {
        return ocrFields
            .stream()
            .filter(f -> emailFields.containsKey(f.getName()))
            .filter(f -> !StringUtils.isEmpty(f.getValue()))
            .filter(f -> isNotValid(f.getValue()))
            .map(f -> format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, emailFields.get(f.getName()), f.getName()))
            .collect(toList());
    }

    private boolean isNotValid(final String email) {
        return !EmailValidator.getInstance().isValid(email);
    }
}
