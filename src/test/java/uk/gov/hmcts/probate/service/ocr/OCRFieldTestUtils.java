package uk.gov.hmcts.probate.service.ocr;

import uk.gov.hmcts.bulkscan.type.OcrDataField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OCRFieldTestUtils {

    public List<OcrDataField> addIHTMandatoryFields() {
        return new ArrayList<>() {
            {
                add(new OcrDataField("ihtFormCompletedOnline", "false"));
                add(new OcrDataField("ihtFormId", "C"));
                add(new OcrDataField("ihtGrossValue", "220.30"));
                add(new OcrDataField("ihtNetValue", "215.50"));
            }
        };
    }

    public List<OcrDataField> addPrimaryApplicantFields() {
        return new ArrayList<>() {
            {
                add(new OcrDataField("primaryApplicantForenames", "Bob"));
                add(new OcrDataField("primaryApplicantSurname", "Smith"));
                add(new OcrDataField("primaryApplicantAddressLine1", "123 Alphabet Street"));
                add(new OcrDataField("primaryApplicantAddressPostCode", "NW1 5LE"));
            }
        };
    }

    public List<OcrDataField> addDeceasedMandatoryFields() {
        return new ArrayList<>() {
            {
                add(new OcrDataField("deceasedForenames", "John"));
                add(new OcrDataField("deceasedSurname", "Johnson"));
                add(new OcrDataField("deceasedAddressLine1", "Smith"));
                add(new OcrDataField("deceasedAddressPostCode", "NW1 6LE"));
                add(new OcrDataField("deceasedDateOfBirth", "1900-01-01"));
                add(new OcrDataField("deceasedDateOfDeath", "2000-01-01"));
                add(new OcrDataField("deceasedAnyOtherNames", "2000-01-01"));
                add(new OcrDataField("deceasedDomicileInEngWales", "true"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryGORCitizenFields() {
        return new ArrayList<>() {
            {
                addAll(addIHTMandatoryFields());
                addAll(addDeceasedMandatoryFields());
                addAll(addPrimaryApplicantFields());
                addAll(addExecutorNotApplyingFields());
                add(new OcrDataField("primaryApplicantHasAlias", "true"));
                add(new OcrDataField("primaryApplicantAlias", "Jack Johnson"));
                add(new OcrDataField("solsSolicitorIsApplying", "False"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryIntestacyCitizenFields() {
        return new ArrayList<>() {
            {
                addAll(addIHTMandatoryFields());
                addAll(addDeceasedMandatoryFields());
                addAll(addPrimaryApplicantFields());
                add(new OcrDataField("solsSolicitorIsApplying", "False"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryGORSolicitorFields() {
        return new ArrayList<>() {
            {
                addAll(addAllMandatoryGORCitizenFields());
                add(new OcrDataField("solsSolicitorIsApplying", "True"));
                add(new OcrDataField("solsSolicitorRepresentativeName", "Mark Jones"));
                add(new OcrDataField("solsSolicitorFirmName", "MJ Solicitors"));
                add(new OcrDataField("solsSolicitorAppReference", "SOLS123456"));
                add(new OcrDataField("solsSolicitorEmail", "solicitor@probate-test.com"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryIntestacySolicitorFields() {
        return new ArrayList<>() {
            {
                addAll(addAllMandatoryIntestacyCitizenFields());
                add(new OcrDataField("solsSolicitorIsApplying", "True"));
                add(new OcrDataField("solsSolicitorRepresentativeName", "Mark Jones"));
                add(new OcrDataField("solsSolicitorFirmName", "MJ Solicitors"));
                add(new OcrDataField("solsSolicitorAppReference", "SOLS123456"));
                add(new OcrDataField("solsSolicitorEmail", "solicitor@probate-test.com"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryCaveatCitizenFields() {
        return new ArrayList<>() {
            {
                add(new OcrDataField("deceasedForenames", "John"));
                add(new OcrDataField("deceasedSurname", "Johnson"));
                add(new OcrDataField("deceasedDateOfDeath", "2000-01-01"));
                add(new OcrDataField("caveatorForenames", "Montriah"));
                add(new OcrDataField("caveatorSurnames", "Montague"));
                add(new OcrDataField("caveatorAddressLine1", "123 Montague Street"));
                add(new OcrDataField("caveatorAddressPostCode", "NW1 5LE"));
            }
        };
    }

    public List<OcrDataField> addAllMandatoryCaveatSolicitorFields() {
        return new ArrayList<>() {
            {
                addAll(addAllMandatoryCaveatCitizenFields());
                add(new OcrDataField("solsSolicitorRepresentativeName", "Mark Jones"));
                add(new OcrDataField("solsSolicitorFirmName", "MJ Solicitors"));
                add(new OcrDataField("solsSolicitorAppReference", "SOLS123456"));
                add(new OcrDataField("solsSolicitorAddressLine1", "22 Palmer Street"));
                add(new OcrDataField("solsSolicitorAddressPostCode", "NW1 5LA"));
                add(new OcrDataField("solsSolicitorEmail", "solicitor@probate-test.com"));
            }
        };

    }

    public List<OcrDataField> addExecutorNotApplyingFields() {
        return new ArrayList<>() {
            {
                add(new OcrDataField("executorsNotApplying_0_notApplyingExecutorName", "Peter Smith"));
                add(new OcrDataField("executorsNotApplying_0_notApplyingExecutorReason", "Already wealthy"));
            }
        };
    }

    public OcrDataField getOCRFieldByKey(List<OcrDataField> ocrFields, String key) {
        for (OcrDataField ocrField : ocrFields) {
            if (ocrField.name().equalsIgnoreCase(key)) {
                return ocrField;
            }
        }
        return null;
    }

    public HashMap<String, String> addAllFields(List<OcrDataField> ocrFields) {
        HashMap<String, String> ocrFieldValues = new HashMap<>();
        ocrFields.forEach(ocrField -> {
            ocrFieldValues.put(ocrField.name(), ocrField.value());
        });
        return ocrFieldValues;
    }

    public void addAllV2Data(List<OcrDataField> ocrFields) {
        ocrFields.addAll(Arrays.asList(
                new OcrDataField("iht400421completed", "false"),
                new OcrDataField("iht207completed", "false"),
                new OcrDataField("deceasedDiedOnAfterSwitchDate", "true"),
                new OcrDataField("ihtEstateGrossValue", "1,000,000"),
                new OcrDataField("ihtEstateNetValue", "900,000"),
                new OcrDataField("ihtEstateNetQualifyingValue", "800,000")
        ));
    }

    public void removeOCRField(List<OcrDataField> ocrFields, String fieldNameToRemove) {
        OcrDataField fieldToRemove = null;
        for (OcrDataField ocrField : ocrFields) {
            if (fieldNameToRemove.equals(ocrField.name())) {
                fieldToRemove = ocrField;
            }
        }

        if (fieldToRemove != null) {
            ocrFields.remove(fieldToRemove);
        }
    }
}
