package uk.gov.hmcts.probate.service.ocr.pa8a;

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

class PA8ACitizenMandatoryFieldsValidatorTest {
    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @InjectMocks
    private PA8ACitizenMandatoryFieldsValidator pa8ACitizenMandatoryFieldsValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    void testAllMandatoryFieldsPresentPA8A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa8ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testMissingMandatoryFieldsReturnSuccessfullyForPA8A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addDeceasedMandatoryFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa8ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(4, warnings.size());
    }

    @Test
    void testOptionalFieldsNotAddedForPA8A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();
        ocrFields.add(new OcrDataField("non-mandatoryField", "test"));
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa8ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    void testFieldDescriptionIsAddedToMissingValueListForPA8A() {
        List<OcrDataField> ocrFields = ocrFieldTestUtils.addAllMandatoryCaveatCitizenFields();
        ocrFields.remove(ocrFields.size() - 1);
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa8ACitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals("Caveator address postcode (caveatorAddressPostCode) is mandatory.",warnings.get(0));
    }

}
