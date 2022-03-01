package uk.gov.hmcts.probate.service.ocr.pa1a;

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

public class PA1ACommonMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA1ACommonMandatoryFieldsValidator pa1ACommonMandatoryFieldsValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    public void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("ihtFormId");

        pa1ACommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(1, warnings.size());
        assertEquals("IHT form id (ihtFormId) is mandatory.", warnings.get(0));
    }

    @Test
    public void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("ihtFormId");
        ocrFieldValues.put("ihtFormCompletedOnline","true");

        pa1ACommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(1, warnings.size());
        assertEquals("IHT reference number (ihtReferenceNumber) is mandatory.", warnings.get(0));
    }


}