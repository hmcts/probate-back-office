package uk.gov.hmcts.probate.service.ocr.pa8a;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PA8ASolicitorMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA8ASolicitorMandatoryFieldsValidator pa8ASolicitorMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testSolictorMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    void testSolicitorMissingMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorAddressLine1");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorAddressPostCode");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorFirmName");
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(3, warnings.size());
        assertEquals("Solictor address line 1 (solsSolicitorAddressLine1) is mandatory.", warnings.get(0));
        assertEquals("Solictor address postcode (solsSolicitorAddressPostCode) is mandatory.",
                warnings.get(1));
        assertEquals("Solicitors Firm name (solsSolicitorFirmName) is mandatory.", warnings.get(2));
    }
}