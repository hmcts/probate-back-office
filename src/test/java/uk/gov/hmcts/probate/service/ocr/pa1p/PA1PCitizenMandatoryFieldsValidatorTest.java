package uk.gov.hmcts.probate.service.ocr.pa1p;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.ocr.CitizenMandatoryFieldsValidatorV2;
import uk.gov.hmcts.probate.service.ocr.MandatoryFieldsValidatorUtils;
import uk.gov.hmcts.probate.service.ocr.OCRFieldTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PA1PCitizenMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @Mock
    private CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    @InjectMocks
    private PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testAllMandatoryFieldsPresentPA1PCitizenV1() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(false);
        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    void testAllMandatoryFieldsPresentPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields(2);

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);
        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    void testOptionalFieldsNotAddedForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("non-mandatoryField", "test");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    void testFieldDescriptionIsAddedToMissingValueListForPA1Pv1() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("solsSolicitorIsApplying");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }

    @Test
    void testFieldDescriptionIsAddedToMissingValueListForPA1Pv2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields(2);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("solsSolicitorIsApplying");
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);
        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }

    @Test
    void testMissingIHT400421CompletedMandatoryFieldReturnSuccessfullyForPA1Pv2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields(2);

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("iht400421completed");
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);
        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Did you complete an IHT400 and IHT421 form? (iht400421completed) is mandatory.", warnings.get(0));
    }

    @Test
    void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1Pv1() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(false);

        ocrFieldValues.remove("ihtFormId");
        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT form id (ihtFormId) is mandatory.", warnings.get(0));
    }

    @Test
    void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1Pv1() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(false);

        ocrFieldValues.remove("ihtReferenceNumber");
        ocrFieldValues.put("ihtFormCompletedOnline", "true");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT reference number (ihtReferenceNumber) is mandatory.", warnings.get(0));
    }

}
