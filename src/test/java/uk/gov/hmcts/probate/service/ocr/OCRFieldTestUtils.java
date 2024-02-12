package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.probate.model.ocr.OCRField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OCRFieldTestUtils {

    public List<OCRField> addIHTMandatoryFields() {
        List<OCRField> ocrFields = new ArrayList<>();
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

        return ocrFields;
    }

    public List<OCRField> addIHTMandatoryFieldsV2() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField field1 = OCRField.builder()
            .name("iht400421completed")
            .value("false")
            .description("Did you complete an IHT400 and IHT421 form?").build();
        OCRField field2 = OCRField.builder()
            .name("ihtGrossValue")
            .value("220.30")
            .description("Enter the gross value of the estate").build();
        OCRField field3 = OCRField.builder()
            .name("ihtNetValue")
            .value("215.50")
            .description("Enter the net value of the estate").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);

        return ocrFields;
    }

    public List<OCRField> addPrimaryApplicantFields() {
        List<OCRField> ocrFields = new ArrayList<>();
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

        return ocrFields;
    }

    public List<OCRField> addDeceasedMandatoryFields() {
        List<OCRField> ocrFields = new ArrayList<>();
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

        return ocrFields;
    }

    //original is version1
    public List<OCRField> addAllMandatoryGORCitizenFields() {
        return addAllMandatoryGORCitizenFields(1);
    }

    public List<OCRField> addAllMandatoryGORCitizenFields(int version) {
        List<OCRField> ocrFields = new ArrayList<>();
        if (3 == version) {
            addAllV3Data(ocrFields);
        } else if (2 == version) {
            ocrFields.addAll(addIHTMandatoryFieldsV2());
            addAllV2Data(ocrFields);
        } else {
            ocrFields.addAll(addIHTMandatoryFields());
        }
        ocrFields.addAll(addDeceasedMandatoryFields());
        ocrFields.addAll(addPrimaryApplicantFields());
        ocrFields.addAll(addExecutorNotApplyingFields());
        OCRField field1 = OCRField.builder()
            .name("primaryApplicantHasAlias")
            .value("true")
            .description("Primary applicant has alias").build();
        OCRField field2 = OCRField.builder()
            .name("primaryApplicantAlias")
            .value("Jack Johnson")
            .description("Primary applicant alias name").build();
        OCRField field3 = OCRField.builder()
            .name("solsSolicitorIsApplying")
            .value("False")
            .description("Solicitor Applying").build();
        OCRField field4 = OCRField.builder()
                .name("formVersion")
                .value("2")
                .description("Form version").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);

        return ocrFields;
    }

    public List<OCRField> addAllMandatoryIntestacyCitizenFields() {
        List<OCRField> ocrFields = new ArrayList<>();

        ocrFields.addAll(addIHTMandatoryFields());
        ocrFields.addAll(addDeceasedMandatoryFields());
        ocrFields.addAll(addPrimaryApplicantFields());
        OCRField field1 = OCRField.builder()
            .name("solsSolicitorIsApplying")
            .value("False")
            .description("Solicitor Applying").build();
        OCRField field2 = OCRField.builder()
                .name("formVersion")
                .value("2")
                .description("Form version").build();
        ocrFields.add(field1);
        ocrFields.add(field2);

        return ocrFields;

    }

    public List<OCRField> addAllMandatoryGORSolicitorFields() {
        List<OCRField> ocrFields = new ArrayList<>();
        ocrFields.addAll(addAllMandatoryGORCitizenFields());
        OCRField field1 = OCRField.builder()
            .name("solsSolicitorIsApplying")
            .value("True")
            .description("Solicitor Applying").build();
        OCRField field2 = OCRField.builder()
            .name("solsSolicitorRepresentativeName")
            .value("Mark Jones")
            .description("Solicitor Representative Name").build();
        OCRField field3 = OCRField.builder()
            .name("solsSolicitorFirmName")
            .value("MJ Solicitors")
            .description("Solicitor Firm Name").build();
        OCRField field4 = OCRField.builder()
            .name("solsSolicitorAppReference")
            .value("SOLS123456")
            .description("Solicitor App Reference").build();
        OCRField field5 = OCRField.builder()
            .name("solsSolicitorEmail")
            .value("solicitor@probate-test.com")
            .description("Solicitor Email Address").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);

        return ocrFields;
    }

    public List<OCRField> addAllMandatoryIntestacySolicitorFields() {
        List<OCRField> ocrFields = new ArrayList<>();
        ocrFields.addAll(addAllMandatoryIntestacyCitizenFields());
        OCRField field1 = OCRField.builder()
            .name("solsSolicitorIsApplying")
            .value("True")
            .description("Solicitor Applying").build();
        OCRField field2 = OCRField.builder()
            .name("solsSolicitorRepresentativeName")
            .value("Mark Jones")
            .description("Solicitor Representative Name").build();
        OCRField field3 = OCRField.builder()
            .name("solsSolicitorFirmName")
            .value("MJ Solicitors")
            .description("Solicitor Firm Name").build();
        OCRField field4 = OCRField.builder()
            .name("solsSolicitorAppReference")
            .value("SOLS123456")
            .description("Solicitor App Reference").build();
        OCRField field5 = OCRField.builder()
            .name("solsSolicitorEmail")
            .value("solicitor@probate-test.com")
            .description("Solicitor Email Address").build();

        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);

        return ocrFields;
    }

    public List<OCRField> addAllMandatoryCaveatCitizenFields() {
        List<OCRField> ocrFields = new ArrayList<>();
        OCRField field0 = OCRField.builder()
            .name("legalRepresentative")
            .value("false")
            .description("Legal Representative").build();
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

        ocrFields.add(field0);
        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);
        ocrFields.add(field5);
        ocrFields.add(field6);
        ocrFields.add(field7);

        return ocrFields;
    }

    public List<OCRField> addAllMandatoryCaveatSolicitorFields() {
        List<OCRField> ocrFields = new ArrayList<>();
        ocrFields.addAll(addAllMandatoryCaveatCitizenFields());
        OCRField field0 = OCRField.builder()
            .name("legalRepresentative")
            .value("true")
            .description("Legal Representative").build();
        OCRField field1 = OCRField.builder()
            .name("solsSolicitorRepresentativeName")
            .value("Mark Jones")
            .description("Solicitor Representative Name").build();
        OCRField field2 = OCRField.builder()
            .name("solsSolicitorFirmName")
            .value("MJ Solicitors")
            .description("Solicitor Firm Name").build();
        OCRField field3 = OCRField.builder()
            .name("solsSolicitorAddressLine1")
            .value("22 Palmer Street")
            .description("Solicitor address building and street").build();
        OCRField field4 = OCRField.builder()
            .name("solsSolicitorAddressPostCode")
            .value("NW1 5LA")
            .description("Solicitor address postcode").build();

        ocrFields.add(field0);
        ocrFields.add(field1);
        ocrFields.add(field2);
        ocrFields.add(field3);
        ocrFields.add(field4);

        return ocrFields;
    }


    public List<OCRField> addExecutorNotApplyingFields() {
        List<OCRField> ocrFields = new ArrayList<>();
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

        return ocrFields;
    }

    public OCRField getOCRFieldByKey(List<OCRField> ocrFields, String key) {
        for (OCRField ocrField : ocrFields) {
            if (ocrField.getName().equalsIgnoreCase(key)) {
                return ocrField;
            }
        }
        return null;
    }

    public HashMap<String, String> addAllFields(List<OCRField> ocrFields) {
        HashMap<String, String> ocrFieldValues = new HashMap<>();
        ocrFields.forEach(ocrField -> ocrFieldValues.put(ocrField.getName(), ocrField.getValue()));
        return ocrFieldValues;
    }

    public void addAllV2Data(List<OCRField> ocrFields) {
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

    }

    public void addAllV3Data(List<OCRField> ocrFields) {
        OCRField iht400completed = OCRField.builder()
                .name("iht400completed")
                .value("true")
                .description("IHT400").build();
        OCRField formVersion = OCRField.builder()
                .name("formVersion")
                .value("3")
                .description("Form version").build();
        OCRField deceasedDiedOnAfterSwitchDate = OCRField.builder()
                .name("deceasedDiedOnAfterSwitchDate")
                .value("true")
                .description("deceasedDiedOnAfterSwitchDate").build();
        OCRField probateGrossValueiht400 = OCRField.builder()
                .name("probateGrossValueIht400")
                .value("1,000,000")
                .description("The probate values from the HMRC letter Gross value").build();
        OCRField probateNetValueiht400 = OCRField.builder()
                .name("probateNetValueIht400")
                .value("900,000")
                .description("The probate values from the HMRC letter net value").build();
        OCRField iht400process = OCRField.builder()
                .name("iht400process")
                .value("true")
                .description("Have you received a letter from HMRC with your unique probate code?").build();
        OCRField ihtcode = OCRField.builder()
                .name("ihtCode")
                .value("CTS 0405231104 3tpp s8e9")
                .description("Free text box for HMRC code").build();
        ocrFields.add(iht400completed);
        ocrFields.add(formVersion);
        ocrFields.add(deceasedDiedOnAfterSwitchDate);
        ocrFields.add(probateGrossValueiht400);
        ocrFields.add(probateNetValueiht400);
        ocrFields.add(iht400process);
        ocrFields.add(ihtcode);
    }

    public void removeOCRField(List<OCRField> ocrFields, String fieldNameToRemove) {
        OCRField fieldToRemove = null;
        for (OCRField ocrField : ocrFields) {
            if (fieldNameToRemove.equals(ocrField.getName())) {
                fieldToRemove = ocrField;
            }
        }

        if (fieldToRemove != null) {
            ocrFields.remove(fieldToRemove);
        }
    }
}
