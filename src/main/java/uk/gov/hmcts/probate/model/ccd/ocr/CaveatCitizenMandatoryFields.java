package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatCitizenMandatoryFields {
    CAVEATOR_FORNAMES("caveatorForenames", "Caveator forenames"),
    CAVEATOR_SURNAME("caveatorSurnames", "Caveator last name"),
    CAVEATOR_ADDRESS_LINE1("caveatorAddressLine1", "Caveator address line 1"),
    CAVEATOR_ADDRESS_POSTCODE("caveatorAddressPostCode", "Caveator address postcode"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death");

    private final String key;
    private final String value;

    CaveatCitizenMandatoryFields(String key, String value) {
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
