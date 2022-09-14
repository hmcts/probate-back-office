package uk.gov.hmcts.probate.service.ocr;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
import uk.gov.hmcts.probate.service.EmailValidationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class OcrEmailValidator {

    protected static final String S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS =
        "%s (%s) does not appear to be a valid email address";
    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS = "primaryApplicantEmailAddress";
    private static final String CAVEATOR_EMAIL_ADDRESS = "caveatorEmailAddress";
    private static final String SOLS_SOLICITOR_EMAIL = "solsSolicitorEmail";
    private static final String SECOND_APPLYING_EMAIL_ADDRESS = "executorsApplying_0_applyingExecutorEmail";
    private static final String THIRD_APPLYING_EMAIL_ADDRESS = "executorsApplying_1_applyingExecutorEmail";
    private static final String FOURTH_APPLYING_EMAIL_ADDRESS = "executorsApplying_2_applyingExecutorEmail";
    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION = "Primary applicant email address";
    private static final String CAVEATOR_EMAIL_ADDRESS_DESCRIPTION = "Caveator email address";
    private static final String SOLS_SOLICITOR_EMAIL_DESCRIPTION = "Solicitor email address";
    private static final String SECOND_APPLYING_EMAIL_ADDRESS_DESCRIPTION = "Second applicant email address";
    private static final String THIRD_APPLYING_EMAIL_DESCRIPTION = "Third applicant email address";
    private static final String FOURTH_APPLYING_EMAIL_DESCRIPTION = "Fourth applicant email address";

    private static final Map<String, String> emailFields = new HashMap<>();
    private final EmailValidationService emailValidationService = new EmailValidationService();

    static {
        emailFields.put(PRIMARY_APPLICANT_EMAIL_ADDRESS, PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(CAVEATOR_EMAIL_ADDRESS, CAVEATOR_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(SOLS_SOLICITOR_EMAIL, SOLS_SOLICITOR_EMAIL_DESCRIPTION);
        emailFields.put(SECOND_APPLYING_EMAIL_ADDRESS, SECOND_APPLYING_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(THIRD_APPLYING_EMAIL_ADDRESS, THIRD_APPLYING_EMAIL_DESCRIPTION);
        emailFields.put(FOURTH_APPLYING_EMAIL_ADDRESS, FOURTH_APPLYING_EMAIL_DESCRIPTION);
    }

    public List<String> validateField(List<OcrDataField> ocrFields) {
        return ocrFields
            .stream()
            .filter(f -> emailFields.containsKey(f.name()))
            .filter(f -> isNotValid(f.value()))
            .map(
                f -> format(S_S_DOES_NOT_APPEAR_TO_BE_A_VALID_EMAIL_ADDRESS, emailFields.get(f.name()), f.name()))
            .collect(toList());
    }

    private boolean isNotValid(final String email) {
        return !emailValidationService.validateEmailAddress(email);
    }
}
