package uk.gov.hmcts.probate.service.ocr.pa1p;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PA1PSolicitorMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA1PSolicitorMandatoryFieldsValidator pa1PSolicitorMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1PSolicitor() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testSolicitorMissingMandatoryFieldsPA1P() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("solsSolicitorIsApplying", "True");

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(4, warnings.size());
        assertEquals("Solicitor representative name (solsSolicitorRepresentativeName) is mandatory.",
            warnings.get(0));
        assertEquals("Solicitors Firm name (solsSolicitorFirmName) is mandatory.", warnings.get(1));
        assertEquals("Solictor application reference (solsSolicitorAppReference) is mandatory.",
            warnings.get(2));
        assertEquals("Solictor email address (solsSolicitorEmail) is mandatory.", warnings.get(3));
    }

    @Test
    void testSolicitorMissingPaymentMethodFieldsPA1P() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("paperPaymentMethod", "PBA");

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Solicitors fee account number (solsFeeAccountNumber) is mandatory.", warnings.get(0));
    }

}
