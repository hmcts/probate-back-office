package uk.gov.hmcts.probate.service.ocr;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OCRToCCDMandatoryFieldTest {

    private List<OCRField> ocrFields;
    private OCRToCCDMandatoryField ocrToCCDMandatoryField = new OCRToCCDMandatoryField();

    @Before
    public void setup() {
        ocrFields = new ArrayList<>();
    }

    @Test
    public void testAllMandatoryFieldsPresent() {
        addAllMandatoryFields();
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields).size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfully() {
        addDeceasedMandatoryFields();
        assertEquals(3, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields).size());
    }

    @Test
    public void testOptionalFieldsNotAdded() {
        addAllMandatoryFields();
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields).size());
    }

    @Test
    public void testFieldNameIsAddedToMissingValueList() {
        addAllMandatoryFields();
        ocrFields.remove(ocrFields.size() - 1);
        assertEquals("Primary applicant Building & Street", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields).get(0));
    }

    private void addAllMandatoryFields() {
        addDeceasedMandatoryFields();
        OCRField field1 = OCRField.builder()
                .name("primaryApplicantForenames")
                .value("Bob")
                .description("Primary applicant forename").build();
        OCRField field2 = OCRField.builder()
                .name("primaryApplicantSurname")
                .value("Smith")
                .description("Primary applicant surname").build();
        OCRField field3 =
                OCRField.builder()
                        .name("primaryApplicantAddress_AddressLine1")
                        .value("123 Alphabet Street")
                        .description("Primary applicant Building & Street").build();


        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
    }

    private void addDeceasedMandatoryFields() {
        OCRField field1 = OCRField.builder()
                .name("deceasedForenames")
                .value("John")
                .description("Deceased forename").build();
        OCRField field2 = OCRField.builder()
                .name("deceasedSurname")
                .value("Johnson")
                .description("Deceased surname").build();
        OCRField field3 = OCRField.builder()
                .name("deceasedAddress_AddressLine1")
                .value("Smith")
                .description("Deceased address").build();
        OCRField field4 = OCRField.builder()
                .name("deceasedDateOfBirth")
                .value("1900-01-01")
                .description("Deceased DOB").build();
        OCRField field5 = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("2000-01-01")
                .description("Deceased DOD").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
    }
}
