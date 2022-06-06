package uk.gov.hmcts.probate.service.ocr.pa1a;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

class PA1ACommonMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA1ACommonMandatoryFieldsValidator pa1ACommonMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testNoCompletedOnlineKeyReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        ocrFieldTestUtils.removeOCRField(ocrFields, "ihtFormCompletedOnline");
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1ACommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("ihtFormId");

        pa1ACommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(1, warnings.size());
        assertEquals("IHT form id (ihtFormId) is mandatory.", warnings.get(0));
    }

    @Test
    void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("ihtFormId");
        ocrFieldValues.put("ihtFormCompletedOnline","true");

        pa1ACommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(1, warnings.size());
        assertEquals("IHT reference number (ihtReferenceNumber) is mandatory.", warnings.get(0));
    }


}
