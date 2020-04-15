package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatSolicitorMandatoryFields {
    CAVEATOR_FORNAMES("caveatorForenames", "Caveator forenames"),
    CAVEATOR_SURNAME("caveatorSurnames", "Caveator last name"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death"),
    SOLICITOR_ADDRESS_LINE1("solsSolicitorAddressLine1", "Solictor address line 1"),
    SOLICITOR_ADDRESS_POSTCODE("solsSolicitorAddressPostCode", "Solictor address postcode"),
    SOLICITOR_FIRM_NAME("solsSolicitorFirmName", "Solicitors Firm name"),
    SOLICITOR_APP_REFERENCE("solsSolicitorAppReference", "Solictor application reference");

    private final String key;
    private final String value;

    CaveatSolicitorMandatoryFields(String key, String value) {
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
