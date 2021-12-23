package uk.gov.hmcts.probate.service.ocr.pa1p;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;
import uk.gov.hmcts.probate.service.ocr.pa1p.PA1PCitizenMandatoryFieldsValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PA1PCitizenMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1PCitizen() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ArrayList<String> warnings = new ArrayList<>();

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ArrayList<String> warnings = new ArrayList<>();
        ocrFieldValues.put("non-mandatoryField", "test");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ArrayList<String> warnings = new ArrayList<>();
        ocrFieldValues.remove("solsSolicitorIsApplying");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ArrayList<String> warnings = new ArrayList<>();
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT 400421 completed (iht400421completed) is mandatory", warnings.get(0));

    }


}