package uk.gov.hmcts.probate.model.ccd.ocr;

public enum GORMandatoryFields {
    PRIMARY_APPLICANT_FORENAMES("primaryApplicantForenames", "Primary applicant First name(s)"),
    PRIMARY_APPLICANT_SURNAME("primaryApplicantSurname", "Primary applicant last name(s)"),
    PRIMARY_APPLICANT_ADDRESS_LINE_1("primaryApplicantAddress_AddressLine1", "Primary applicant Building & Street"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased First name(s)"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name(s)"),
    DECEASED_ADDRESS("deceasedAddress_AddressLine1",
            "What was the permanent address of the deceased at the time of their death?"),
    DECEASED_DOB("deceasedDateOfBirth", "What was their date of birth?"),
    DECEASED_DOD("deceasedDateOfDeath", "What was their date of death?");

    private final String key;
    private final String value;

    GORMandatoryFields(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
