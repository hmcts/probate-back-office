package uk.gov.hmcts.probate.service.ocr.pa8a;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PA8ASolicitorMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA8ASolicitorMandatoryFieldsValidator pa8ASolicitorMandatoryFieldsValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }
    
    @Test
    public void testSolictorMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    public void testSolicitorMissingMandatoryFieldsPA8A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorAddressLine1");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorAddressPostCode");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorFirmName");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorAppReference");
        ocrFieldTestUtils.removeOCRField(ocrFields, "solsSolicitorEmail");
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(5, warnings.size());
        assertEquals("Solictor address line 1 (solsSolicitorAddressLine1) is mandatory.", warnings.get(0));
        assertEquals("Solictor address postcode (solsSolicitorAddressPostCode) is mandatory.", warnings.get(1));
        assertEquals("Solicitors Firm name (solsSolicitorFirmName) is mandatory.", warnings.get(2));
        assertEquals("Solictor application reference (solsSolicitorAppReference) is mandatory.", warnings.get(3));
        assertEquals("Solictor email address (solsSolicitorEmail) is mandatory.", warnings.get(4));
    }

    @Test
    public void testSolicitorMissingPaymentMethodFieldsPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("paperPaymentMethod", "PBA");

        pa8ASolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Solicitors fee account number (solsFeeAccountNumber) is mandatory.", warnings.get(0));
    }

}