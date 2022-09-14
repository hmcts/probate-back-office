package uk.gov.hmcts.probate.service.ocr.pa1a;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.bulkscan.type.OcrDataField;
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

class PA1ACitizenMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @Mock
    private CitizenMandatoryFieldsValidatorV2 citizenMandatoryFieldsValidatorV2;

    @InjectMocks
    private PA1ACitizenMandatoryFieldsValidator pa1ACitizenMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testAllMandatoryFieldsPresentPA1A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testMissingMandatoryFieldsReturnSuccessfullyForPA1A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addDeceasedMandatoryFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(8, warnings.size());
    }

    @Test
    void testOptionalFieldsNotAddedForPA1A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("non-mandatoryField", "test");

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
        ocrFields.add(new OcrDataField("non-mandatoryField", "test"));
    }

    @Test
    void testFieldDescriptionIsAddedToMissingValueListForPA1A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("solsSolicitorIsApplying");

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }

    @Test
    void testAllMandatoryFieldsPresentPA1ACitizenV2() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        ocrFieldTestUtils.addAllV2Data(ocrFields);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        verify(citizenMandatoryFieldsValidatorV2).addWarnings(any(), any());
        assertEquals(0, warnings.size());
    }

    @Test
    void testMissingMandatoryFieldsForPA1ACitizenV2() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryIntestacyCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        pa1ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        verify(citizenMandatoryFieldsValidatorV2).addWarnings(any(), any());
        assertEquals(1, warnings.size());
        assertEquals("Did you complete an IHT400 and IHT421 form? (iht400421completed) is mandatory.", warnings.get(0));

    }

}
