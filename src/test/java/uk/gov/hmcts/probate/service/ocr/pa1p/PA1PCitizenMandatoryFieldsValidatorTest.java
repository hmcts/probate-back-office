package uk.gov.hmcts.probate.service.ocr.pa1p;

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
import static org.mockito.Mockito.when;

public class PA1PCitizenMandatoryFieldsValidatorTest {

    private OCRFieldTestUtils ocrFieldTestUtils = new OCRFieldTestUtils();
    private ArrayList<String> warnings;

    @Mock
    private MandatoryFieldsValidatorUtils mandatoryFieldsValidatorUtils;

    @InjectMocks
    private PA1PCitizenMandatoryFieldsValidator pa1PCitizenMandatoryFieldsValidator;

    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        warnings = new ArrayList<>();
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1PCitizen() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.put("non-mandatoryField", "test");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);
        assertEquals(0, warnings.size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1P() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        ocrFieldValues.remove("solsSolicitorIsApplying");

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("Do you have legal representative acting for you? (solsSolicitorIsApplying) is mandatory.",
            warnings.get(0));
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        OCRField iht400421completed = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("IHT Completed online?").build();
        OCRField iht207completed = OCRField.builder()
            .name("iht207completed")
            .value("false")
            .description("IHT Completed").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
            .name("deceasedDiedOnAfterSwitchDate")
            .value("true")
            .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField ihtEstateGrossValue = OCRField.builder()
            .name("ihtEstateGrossValue")
            .value("1,000,000")
            .description("ihtEstateGrossValue").build();
        OCRField ihtEstateNetValue = OCRField.builder()
            .name("ihtEstateNetValue")
            .value("900,000")
            .description("ihtEstateNetValue").build();
        OCRField ihtEstateNetQualifyingValue = OCRField.builder()
            .name("ihtEstateNetQualifyingValue")
            .value("800,000")
            .description("ihtEstateNetQualifyingValue").build();
        ocrFields.add(iht400421completed);
        ocrFields.add(iht207completed);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(ihtEstateGrossValue);
        ocrFields.add(ihtEstateNetValue);
        ocrFields.add(ihtEstateNetQualifyingValue);

        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(0, warnings.size());
    }

    @Test
    public void testMissingMandatoryFieldsPresentPA1PCitizenV2() {
        List<OCRField> ocrFields = ocrFieldTestUtils.addAllMandatoryGORCitizenFields();
        HashMap<String, String> ocrFieldValues = ocrFieldTestUtils.addAllFields(ocrFields);
        when(mandatoryFieldsValidatorUtils.isVersion2(ocrFieldValues)).thenReturn(true);

        pa1PCitizenMandatoryFieldsValidator.addWarnings(ocrFieldValues, warnings);

        assertEquals(1, warnings.size());
        assertEquals("IHT 400421 completed (iht400421completed) is mandatory.", warnings.get(0));

    }

}