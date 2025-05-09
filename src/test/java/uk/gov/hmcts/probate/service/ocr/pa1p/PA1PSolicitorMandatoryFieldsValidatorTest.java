package uk.gov.hmcts.probate.service.ocr.pa1p;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PA1PSolicitorMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;
    @InjectMocks
    private PA1PSolicitorMandatoryFieldsValidator pa1PSolicitorMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1PSolicitor() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testSolicitorAllMandatoryFieldsPresentPA1PSolicitorV3() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("deceasedDiedOnAfterSwitchDate", "True");
        when(mandatoryFieldsValidatorUtils.isVersion3(ocrFieldValues)).thenReturn(true);

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testSolicitorMissingPaymentMethodFieldsPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORSolicitorFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("paperPaymentMethod", "PBA");

        pa1PSolicitorMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Solicitors fee account number (solsFeeAccountNumber) is mandatory.", warnings.get(0));
    }

}
