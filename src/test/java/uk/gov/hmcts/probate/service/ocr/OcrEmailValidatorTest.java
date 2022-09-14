package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;

class OcrEmailValidatorTest {

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

    private static Map<String, String> emailFields = new HashMap<>();

    static {
        emailFields.put(PRIMARY_APPLICANT_EMAIL_ADDRESS, PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(CAVEATOR_EMAIL_ADDRESS, CAVEATOR_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(SOLS_SOLICITOR_EMAIL, SOLS_SOLICITOR_EMAIL_DESCRIPTION);
        emailFields.put(SECOND_APPLYING_EMAIL_ADDRESS, SECOND_APPLYING_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(THIRD_APPLYING_EMAIL_ADDRESS, THIRD_APPLYING_EMAIL_DESCRIPTION);
        emailFields.put(FOURTH_APPLYING_EMAIL_ADDRESS, FOURTH_APPLYING_EMAIL_DESCRIPTION);
    }

    private OcrEmailValidator ocrEmailValidator;

    @BeforeEach
    public void setUp() {
        ocrEmailValidator = new OcrEmailValidator();
    }


    @Test
    void shouldCreateWarningForAnInvalidField() {

        final OcrDataField field = new OcrDataField(PRIMARY_APPLICANT_EMAIL_ADDRESS, RandomStringUtils.randomAlphabetic(10));

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    void shouldCreateWarningForEachInvalidField() {
        final List<OcrDataField> fields = emailFields
            .keySet()
            .stream()
            .map(f -> new OcrDataField(f, RandomStringUtils.randomAlphabetic(10)))
            .collect(toList());

        final List<String> result = ocrEmailValidator.validateField(fields);
        assertThat(result.size(), is(6));

        result.forEach(r -> assertWarning(result, fields.get(0)));
        fields.forEach(f -> assertWarning(result, f));
    }

    @Test
    void shouldNotCreateWarningForValidField() {
        final List<OcrDataField> fields = emailFields
            .keySet()
            .stream()
            .map(f -> new OcrDataField(f, RandomStringUtils.randomAlphabetic(10) + "@probate-test.com"))
            .collect(toList());

        final List<String> result = ocrEmailValidator.validateField(fields);
        assertThat(result, is(empty()));
    }

    @Test
    void shouldCreateWarningForNullField() {

        final OcrDataField field = new OcrDataField(PRIMARY_APPLICANT_EMAIL_ADDRESS, null);

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    void shouldCreateWarningForEmptyField() {

        final OcrDataField field = new OcrDataField(PRIMARY_APPLICANT_EMAIL_ADDRESS, "");

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));

        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    void shouldNotCreateWarningNonEmailField() {
        final OcrDataField field = new OcrDataField(
                "executorsNotApplying_0_notApplyingExecutorName",
                "Peter Smith"
        );

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result, is(empty()));
    }

    private void assertWarning(final List<String> result, final OcrDataField ocrField) {
        assertThat(result, hasItem(
            format("%s (%s) does not appear to be a valid email address", emailFields.get(ocrField.name()),
                ocrField.name())));
    }
}
