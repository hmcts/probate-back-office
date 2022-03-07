package uk.gov.hmcts.probate.service.ocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_NET_VALUE;
import uk.gov.hmcts.probate.model.ocr.OCRField;

public class MandatoryFieldsValidatorUtilsTest {
    
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    private List<String> warnings;
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private List<OCRField> ocrFields;

    @Before
    public void setup() {
        mandatoryFieldsValidatorUtils = new MandatoryFieldsValidatorUtils();
        warnings = new ArrayList<>();
        ocrFields = new ArrayList<>();
    }

    @Test
    public void shouldAddWarningIfEmpty() {
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        DefaultKeyValue keyValue = new DefaultKeyValue("testKey", null);
        mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldValues, warnings, keyValue);
        assertEquals(1, warnings.size());
        assertEquals("null (testKey) is mandatory.", warnings.get(0));
    }

    @Test
    public void shouldAddWarningsForConditionalFields() {
        HashMap ocrFieldValues = new HashMap();
        mandatoryFieldsValidatorUtils
            .addWarningsForConditionalFields(ocrFieldValues, warnings, IHT_GROSS_VALUE, IHT_NET_VALUE);
        assertEquals(2, warnings.size());
    }
    
    @Test
    public void shouldAddWarning() {
        String warning = "Test";
        mandatoryFieldsValidatorUtils.addWarning("Test", warnings);
        assertEquals(1, warnings.size());
        assertEquals(warning, warnings.get(0));
    }

    @Test
    public void shouldReturnTrueIfVersion2() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField formVersion = OCRField.builder()
            .name("formVersion")
            .value("2")
            .build();
        ocrFields.add(formVersion);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        assertTrue(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues));
    }
}