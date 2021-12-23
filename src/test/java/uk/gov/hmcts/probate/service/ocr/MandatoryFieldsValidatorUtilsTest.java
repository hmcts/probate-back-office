package uk.gov.hmcts.probate.service.ocr;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.DECEASED_LATE_SPOUSE;
import static uk.gov.hmcts.probate.model.ccd.ocr.GORCitizenMandatoryFields.IHT_UNUSED_ALLOWANCE;

public class MandatoryFieldsValidatorUtilsTest {

    @InjectMocks
    MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnVersion2() {
        HashMap<String, String> ocrFieldsMap = new HashMap<>();
        ocrFieldsMap.put("formVersion", "2");

        assertTrue(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldsMap));
    }

    @Test
    public void shouldNotReturnVersion2() {
        HashMap<String, String> ocrFieldsMap = new HashMap<>();
        ocrFieldsMap.put("formVersion", "1");

        assertFalse(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldsMap));

        ocrFieldsMap.put("formVersion", "");
        assertFalse(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldsMap));

        ocrFieldsMap.remove("formVersion");
        assertFalse(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldsMap));
    }

    @Test
    public void shouldAddWairningsIfEmpty() {
        HashMap<String, String> ocrFieldsMap = new HashMap<>();
        ocrFieldsMap.put("key1", "");

        ArrayList<String> warnings = new ArrayList<>();
        DefaultKeyValue keyValue = new DefaultKeyValue("key1", "value1");
        mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldsMap, warnings, keyValue);
        assertEquals(1, warnings.size());
        assertEquals("value1 (key1) is mandatory.", warnings.get(0));
    }

    @Test
    public void shouldNotAddWairningsIfNotEmpty() {
        HashMap<String, String> ocrFieldsMap = new HashMap<>();
        ocrFieldsMap.put("key1", "value1");

        ArrayList<String> warnings = new ArrayList<>();
        DefaultKeyValue keyValue = new DefaultKeyValue("key1", "value1");
        mandatoryFieldsValidatorUtils.addWarningIfEmpty(ocrFieldsMap, warnings, keyValue);
        assertEquals(0, warnings.size());
    }

    @Test
    public void shouldAddWarningsForMissingConditionalFields() {
        HashMap<String, String> ocrFieldsMap = new HashMap<>();
        ocrFieldsMap.put("ihtUnusedAllowanceClaimed", "true");

        ArrayList<String> warnings = new ArrayList<>();
        mandatoryFieldsValidatorUtils.addWarningsForConditionalFields(ocrFieldsMap, warnings, IHT_UNUSED_ALLOWANCE,
            DECEASED_LATE_SPOUSE);
        assertEquals(1, warnings.size());
        assertEquals("What was the marital status of the person who has died when they died? WIDOWED "
            + "(deceasedHadLateSpouseOrCivilPartner) is mandatory.", warnings.get(0));
    }

}