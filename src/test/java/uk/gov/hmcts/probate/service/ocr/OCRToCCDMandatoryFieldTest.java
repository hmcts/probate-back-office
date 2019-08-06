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
    public void testAllMandatoryFieldsPresentPA1P() {
        addAllMandatoryGORFields();
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfullyForPA1P() {
        addDeceasedMandatoryFields();
        assertEquals(3, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1P() {
        addAllMandatoryGORFields();
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1P() {
        addAllMandatoryGORFields();
        ocrFields.remove(ocrFields.size() - 1);
        assertEquals("Primary applicant Building & Street", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
                FormType.PA1P
        ).get(0));
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1A() {
        addAllMandatoryGORFields();
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfullyForPA1A() {
        addDeceasedMandatoryFields();
        assertEquals(3, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1A() {
        addAllMandatoryGORFields();
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1A() {
        addAllMandatoryGORFields();
        ocrFields.remove(ocrFields.size() - 1);
        assertEquals("Primary applicant Building & Street", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
                FormType.PA1A
        ).get(0));
    }

    @Test
    public void testAllMandatoryFieldsPresentPA8A() {
        addAllCaveatMandatoryFields();
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A).size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfullyForPA8A() {
        addDeceasedMandatoryFields();
        assertEquals(3, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A).size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA8A() {
        addAllCaveatMandatoryFields();
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A).size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA8A() {
        addAllCaveatMandatoryFields();
        ocrFields.remove(ocrFields.size() - 1);
        assertEquals("Caveator address building and street", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
                FormType.PA8A
        ).get(0));
    }


    private void addAllMandatoryGORFields() {
        addDeceasedMandatoryFields();
        OCRField field1 = OCRField.builder()
                .name("primaryApplicantForenames")
                .value("Bob")
                .description("Primary applicant forename").build();
        OCRField field2 = OCRField.builder()
                .name("primaryApplicantSurname")
                .value("Smith")
                .description("Primary applicant surname").build();
        OCRField field3 = OCRField.builder()
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

    private void addAllCaveatMandatoryFields() {
        OCRField field1 = OCRField.builder()
                .name("deceasedForenames")
                .value("John")
                .description("Deceased forename").build();
        OCRField field2 = OCRField.builder()
                .name("deceasedSurname")
                .value("Johnson")
                .description("Deceased surname").build();
        OCRField field3 = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("2000-01-01")
                .description("Deceased DOD").build();
        OCRField field4 = OCRField.builder()
                .name("caveatorForenames")
                .value("Montriah")
                .description("Forenames(s)").build();
        OCRField field5 = OCRField.builder()
                .name("caveatorSurname")
                .value("Montague")
                .description("Surname(s)").build();
        OCRField field6 = OCRField.builder()
                .name("caveatorAddress_AddressLine1")
                .value("123 Montague Street")
                .description("Caveator address building and street").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
        ocrFields.add(field6);
    }
}
