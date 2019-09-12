package uk.gov.hmcts.probate.service.ocr;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.ocr.CaveatMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.GORMandatoryFields;
import uk.gov.hmcts.probate.model.ccd.ocr.IntestacyMandatoryFields;
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
        assertEquals(8, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P).size());
    }

    @Test
    public void testMissingNotApplyingMandatoryFieldReturnSuccessfullyForPA1P() {
        addAllMandatoryGORFields();
        ocrFields.remove(getOCRFieldByKey(ocrFields,"executorsNotApplying_0_notApplyingExecutorReason"));
        List<String> results = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P);
        assertEquals(1, results.size());
        assertEquals("Key 'executorsNotApplying_0_notApplyingExecutorReason' is mandatory.", results.get(0));
    }

    @Test
    public void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1P() {
        addAllMandatoryGORFields();
        ocrFields.remove(getOCRFieldByKey(ocrFields,"ihtFormId"));
        List<String> results = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P);
        assertEquals(1, results.size());
        assertEquals("Key 'ihtFormId' is mandatory.", results.get(0));
    }

    @Test
    public void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1P() {
        addAllMandatoryGORFields();
        ocrFields.remove(getOCRFieldByKey(ocrFields,"ihtFormId"));
        ocrFields.add(OCRField.builder().name("ihtFormCompletedOnline").value("true").description("IHT Online?").build());
        List<String> results = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1P);
        assertEquals(1, results.size());
        assertEquals("Key 'ihtReferenceNumber' is mandatory.", results.get(0));
    }

    @Test
    public void testMissingIHTFormIdMandatoryFieldReturnSuccessfullyForPA1A() {
        addAllMandatoryIntestacyFields();
        ocrFields.remove(getOCRFieldByKey(ocrFields,"ihtFormId"));
        List<String> results = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A);
        assertEquals(1, results.size());
        assertEquals("Key 'ihtFormId' is mandatory.", results.get(0));
    }

    @Test
    public void testMissingIHTReferenceMandatoryFieldReturnSuccessfullyForPA1A() {
        addAllMandatoryIntestacyFields();
        ocrFields.remove(getOCRFieldByKey(ocrFields,"ihtFormId"));
        ocrFields.add(OCRField.builder().name("ihtFormCompletedOnline").value("true").description("IHT Online?").build());
        List<String> results = ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A);
        assertEquals(1, results.size());
        assertEquals("Key 'ihtReferenceNumber' is mandatory.", results.get(0));
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
        assertEquals("Key 'primaryApplicantAlias' is mandatory.", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
                FormType.PA1P
        ).get(0));
    }

    @Test
    public void testAllMandatoryFieldsPresentPA1A() {
        addAllMandatoryIntestacyFields();
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testMissingMandatoryFieldsReturnSuccessfullyForPA1A() {
        addDeceasedMandatoryFields();
        assertEquals(7, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testOptionalFieldsNotAddedForPA1A() {
        addAllMandatoryIntestacyFields();
        ocrFields.add(OCRField.builder().name("non-mandatoryField").value("test").description("test").build());
        assertEquals(0, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA1A).size());
    }

    @Test
    public void testFieldDescriptionIsAddedToMissingValueListForPA1A() {
        addAllMandatoryIntestacyFields();
        ocrFields.remove(ocrFields.size() - 1);
        assertEquals("Key 'primaryApplicantAddressPostCode' is mandatory.", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
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
        assertEquals(4, ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields, FormType.PA8A).size());
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
        assertEquals("Key 'caveatorAddressPostCode' is mandatory.", ocrToCCDMandatoryField.ocrToCCDMandatoryFields(ocrFields,
                FormType.PA8A
        ).get(0));
    }

    private void addIHTMandatoryFields() {
        OCRField field1 = OCRField.builder()
                .name("ihtFormCompletedOnline")
                .value("false")
                .description("IHT Completed online?").build();
        OCRField field2 = OCRField.builder()
                .name("ihtFormId")
                .value("C")
                .description("IHT Form Id").build();
        OCRField field3 = OCRField.builder()
                .name("ihtGrossValue")
                .value("220.30")
                .description("Enter the gross value of the estate").build();
        OCRField field4 = OCRField.builder()
                .name("ihtNetValue")
                .value("215.50")
                .description("Enter the net value of the estate").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
    }

    private void addPrimaryApplicantFields() {
        OCRField field1 = OCRField.builder()
                .name("primaryApplicantForenames")
                .value("Bob")
                .description("Primary applicant forename").build();
        OCRField field2 = OCRField.builder()
                .name("primaryApplicantSurname")
                .value("Smith")
                .description("Primary applicant surname").build();
        OCRField field3 = OCRField.builder()
                .name("primaryApplicantAddressLine1")
                .value("123 Alphabet Street")
                .description("Primary applicant Building & Street").build();
        OCRField field4 = OCRField.builder()
                .name("primaryApplicantAddressPostCode")
                .value("NW1 5LE")
                .description("Primary applicant postcode").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
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
                .name("deceasedAddressLine1")
                .value("Smith")
                .description("Deceased address").build();
        OCRField field4 = OCRField.builder()
                .name("deceasedAddressPostCode")
                .value("NW1 6LE")
                .description("Deceased postcode").build();
        OCRField field5 = OCRField.builder()
                .name("deceasedDateOfBirth")
                .value("1900-01-01")
                .description("Deceased DOB").build();
        OCRField field6 = OCRField.builder()
                .name("deceasedDateOfDeath")
                .value("2000-01-01")
                .description("Deceased DOD").build();
        OCRField field7 = OCRField.builder()
                .name("deceasedAnyOtherNames")
                .value("2000-01-01")
                .description("Jack Johnson").build();
        OCRField field8 = OCRField.builder()
                .name("deceasedDomicileInEngWales")
                .value("true")
                .description("Deceased Domicile In England or Wales").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
        ocrFields.add(field6);
        ocrFields.add(field7);
        ocrFields.add(field8);
    }

    private void addAllMandatoryGORFields() {
        addIHTMandatoryFields();
        addDeceasedMandatoryFields();
        addPrimaryApplicantFields();
        addExecutorNotApplyingFields();
        OCRField field1 = OCRField.builder()
                .name("primaryApplicantHasAlias")
                .value("true")
                .description("Primary applicant has alias").build();
        OCRField field2 = OCRField.builder()
                .name("primaryApplicantAlias")
                .value("Jack Johnson")
                .description("Primary applicant alias name").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
    }

    private void addAllMandatoryIntestacyFields() {
        addIHTMandatoryFields();
        addDeceasedMandatoryFields();
        addPrimaryApplicantFields();
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
                .name("caveatorSurnames")
                .value("Montague")
                .description("Surname(s)").build();
        OCRField field6 = OCRField.builder()
                .name("caveatorAddressLine1")
                .value("123 Montague Street")
                .description("Caveator address building and street").build();
        OCRField field7 = OCRField.builder()
                .name("caveatorAddressPostCode")
                .value("NW1 5LE")
                .description("Caveator address postcode").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
        ocrFields.add(field6);
        ocrFields.add(field7);
    }


    private void addExecutorNotApplyingFields() {
        OCRField field1 = OCRField.builder()
                .name("executorsNotApplying_0_notApplyingExecutorName")
                .value("Peter Smith")
                .description("Executor not applying name").build();
        OCRField field2 = OCRField.builder()
                .name("executorsNotApplying_0_notApplyingExecutorReason")
                .value("Already wealthy")
                .description("Executor not applying reason").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
    }

    private OCRField getOCRFieldByKey(List<OCRField> ocrFields, String key) {
        for (OCRField ocrField : ocrFields) {
            if (ocrField.getName().equalsIgnoreCase(key)) {
                return ocrField;
            }
        }
        return null;
    }
}
