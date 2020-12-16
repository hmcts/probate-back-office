package uk.gov.hmcts.probate.service.ocr;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;

import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

public class OcrEmailValidatorTest {

    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS = "primaryApplicantEmailAddress";
    private static final String CAVEATOR_EMAIL_ADDRESS = "caveatorEmailAddress";
    private static final String SOLS_SOLICITOR_EMAIL = "solsSolicitorEmail";
    private static final String PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION = "Primary applicant email address";
    private static final String CAVEATOR_EMAIL_ADDRESS_DESCRIPTION = "Caveator email address";
    private static final String SOLS_SOLICITOR_EMAIL_DESCRIPTION = "Solicitor email address";

    private static Map<String, String> emailFields = new HashMap<>();
    static{
        emailFields.put(PRIMARY_APPLICANT_EMAIL_ADDRESS, PRIMARY_APPLICANT_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(CAVEATOR_EMAIL_ADDRESS, CAVEATOR_EMAIL_ADDRESS_DESCRIPTION);
        emailFields.put(SOLS_SOLICITOR_EMAIL, SOLS_SOLICITOR_EMAIL_DESCRIPTION);
    }

    private OcrEmailValidator ocrEmailValidator;

    @Before
    public void setUp() {
        ocrEmailValidator = new OcrEmailValidator();
    }


    @Test
    public void shouldCreateWarningForAnInvalidField() {

        final OCRField field = OCRField
                .builder()
                .name(PRIMARY_APPLICANT_EMAIL_ADDRESS)
                .value(RandomStringUtils.randomAlphabetic(10))
                .build();

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    public void shouldCreateWarningForEachInvalidField() {
        final List<OCRField> fields = emailFields
                .keySet()
                .stream()
                .map(f -> OCRField
                        .builder()
                        .name(f)
                        .value(RandomStringUtils.randomAlphabetic(10))
                        .build()
                )
                .collect(toList());

        final List<String> result = ocrEmailValidator.validateField(fields);
        assertThat(result.size(), is(3));

        result.forEach(r -> assertWarning(result, fields.get(0)));
        fields.forEach(f -> assertWarning(result, f));
    }

    @Test
    public void shouldNotCreateWarningForValidField() {
        final List<OCRField> fields = emailFields
                .keySet()
                .stream()
                .map(f -> OCRField
                        .builder()
                        .name(f)
                        .value(RandomStringUtils.randomAlphabetic(10) + "@probate-test.com")
                        .build()
                )
                .collect(toList());

        final List<String> result = ocrEmailValidator.validateField(fields);
        assertThat(result, is(empty()));
    }

    @Test
    public void shouldCreateWarningForNullField() {

        final OCRField field = OCRField
                .builder()
                .name(PRIMARY_APPLICANT_EMAIL_ADDRESS)
                .build();

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    public void shouldCreateWarningForEmptyField() {

        final OCRField field = OCRField
                .builder()
                .name(PRIMARY_APPLICANT_EMAIL_ADDRESS)
                .value("")
                .build();

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));

        assertThat(result.size(), is(1));
        assertWarning(result, field);
    }

    @Test
    public void shouldNotCreateWarningNonEmailField() {
        final OCRField field = OCRField
                .builder()
                .name("executorsNotApplying_0_notApplyingExecutorName")
                .value("Peter Smith")
                .build();

        final List<String> result = ocrEmailValidator.validateField(singletonList(field));
        assertThat(result, is(empty()));
    }

    private void assertWarning(final List<String> result, final OCRField ocrField) {
        assertThat(result, hasItem(format("%s (%s) does not appear to be a valid email address", emailFields.get(ocrField.getName()), ocrField.getName())));
    }
}
