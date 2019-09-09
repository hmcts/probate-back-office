package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatMandatoryFields {
    DECEASED_FORENAMES("deceasedForenames", "Deceased first name(s)"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_DOD("deceasedDateOfDeath", "What was their date of death?"),
    CAVEATOR_FORNAMES("caveatorForenames", "Forenames(s)"),
    CAVEATOR_SURNAME("caveatorSurname", "Surname"),
    CAVEATOR_ADDRESS_LINE1("caveatorAddressLine1", "Caveator address building and street"),
    CAVEATOR_ADDRESS_POSTCODE("caveatorAddressPostCode", "Caveator address postcode");

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
