package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatMandatoryFields {
    DECEASED_FORENAMES("deceasedForenames", "Deceased First name(s)"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name(s)"),
    DECEASED_DOD("deceasedDateOfDeath", "What was their date of death?"),
    CAVEATOR_FORNAMES("caveatorForenames", "Forenames(s)"),
    CAVEATOR_SURNAME("caveatorSurname", "Surname(s)"),
    CAVEATOR_ADDRESS("caveatorAddress_AddressLine1", "Caveator address building and street");

    private final String key;
    private final String value;

    CaveatMandatoryFields(String key, String value) {
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
