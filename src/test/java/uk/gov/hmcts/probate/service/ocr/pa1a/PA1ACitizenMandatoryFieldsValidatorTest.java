package uk.gov.hmcts.probate.service.ocr.pa1a;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PA1ACitizenMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private PA1ACitizenMandatoryFieldsValidator pa1ACitizenMandatoryFieldsValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        
        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addDeceasedMandatoryFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(8, warnings.size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("non-mandatoryField", "test");

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("solsSolicitorIsApplying");

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }
}