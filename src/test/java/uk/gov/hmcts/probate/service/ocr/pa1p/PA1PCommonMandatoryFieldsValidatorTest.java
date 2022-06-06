package uk.gov.hmcts.probate.service.ocr.pa1p;

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

class PA1PCommonMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA1PCommonMandatoryFieldsValidator pa1PCommonMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testNoPrimaryApplicantHasAlasKeyReturnSuccessfullyForPA1A() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        ocrFieldTestUtils.removeOCRField(ocrFields, "primaryApplicantHasAlias");
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1PCommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testMissingNotApplyingMandatoryFieldReturnSuccessfullyForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        ocrFieldValues.remove("executorsNotApplying_0_notApplyingExecutorReason");
        pa1PCommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Executor 0 not applying reason (executorsNotApplying_0_notApplyingExecutorReason) is mandatory.",
            warnings.get(0));
    }

    @Test
    void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        ocrFieldValues.remove("ihtFormId");
        pa1PCommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT form id (ihtFormId) is mandatory.", warnings.get(0));
    }

    @Test
    void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        ocrFieldValues.remove("ihtFormId");
        ocrFieldValues.put("ihtFormCompletedOnline", "true");

        pa1PCommonMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT reference number (ihtReferenceNumber) is mandatory.", warnings.get(0));
    }

}
