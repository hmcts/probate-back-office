package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_NET_VALUE;

class MandatoryFieldsValidatorUtilsTest {
    @InjectMocks
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    @Mock
    private IhtEstateValidationRule ihtEstateValidationRule;
    private List<String> warnings;
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private List<OCRField> ocrFields;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mandatoryFieldsValidatorUtils = new MandatoryFieldsValidatorUtils(ihtEstateValidationRule);
        warnings = new ArrayList<>();
        ocrFields = new ArrayList<>();
    }

    @Test
    void shouldAddWarningIfEmpty() {
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        DefaultKeyValue keyValue = new DefaultKeyValue("testKey", null);
        mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, keyValue);
        assertEquals(1, warnings.size());
        assertEquals("null (testKey) is mandatory.", warnings.get(0));
    }

    @Test
    void shouldAddWarningsForConditionalFields() {
        HashMap ocrFieldValues = new HashMap();
        mandatoryFieldsValidatorUtils
            .addWarningsForConditionalFields(ocrFieldValues, warnings, IHT_GROSS_VALUE, IHT_NET_VALUE);
        assertEquals(2, warnings.size());
    }

    @Test
    void shouldAddWarning() {
        String warning = "Test";
        mandatoryFieldsValidatorUtils.addWarning("Test", warnings);
        assertEquals(1, warnings.size());
        assertEquals(warning, warnings.get(0));
    }

    @Test
    void shouldReturnTrueIfVersion2() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField formVersion = OCRField.builder()
            .name("formVersion")
            .value("2")
            .build();
        ocrFields.add(formVersion);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        assertTrue(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues));
    }

    @Test
    void shouldReturnTrueIfVersion3() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField formVersion = OCRField.builder()
                .name("formVersion")
                .value("3")
                .build();
        ocrFields.add(formVersion);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        assertTrue(mandatoryFieldsValidatorUtils.isVersion3(ocrFieldValues));
    }

    @Test
    void shouldReturnTrueWhenNqvBetweenThresholds() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField nqv = OCRField.builder()
                .name("ihtEstateNetQualifyingValue")
                .value("5000")
                .build();
        ocrFields.add(nqv);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any(BigDecimal.class))).thenReturn(true);
        assertTrue(mandatoryFieldsValidatorUtils.nqvBetweenThresholds(ocrFieldValues));
    }

    @Test
    void shouldReturnFalseWhenNqvNotBetweenThresholds() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField nqv = OCRField.builder()
                .name("ihtEstateNetQualifyingValue")
                .value("5000")
                .build();
        ocrFields.add(nqv);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(ihtEstateValidationRule.isNqvBetweenValues(any(BigDecimal.class))).thenReturn(false);
        assertFalse(mandatoryFieldsValidatorUtils.nqvBetweenThresholds(ocrFieldValues));
    }

    @Test
    void shouldReturnFalseForNullNqv() {
        List<OCRField> ocrFields = new ArrayList<>();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        assertFalse(mandatoryFieldsValidatorUtils.nqvBetweenThresholds(ocrFieldValues));
    }
}
