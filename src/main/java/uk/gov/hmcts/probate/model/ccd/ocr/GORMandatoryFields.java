package uk.gov.hmcts.probate.model.ccd.ocr;

public enum GORMandatoryFields {
    PRIMARY_APPLICANT_FORENAMES("primaryApplicantForenames", "Primary applicant first name(s)"),
    PRIMARY_APPLICANT_SURNAME("primaryApplicantSurname", "Primary applicant last name"),
    PRIMARY_APPLICANT_ADDRESS_LINE1("primaryApplicantAddressLine1", "Primary applicant building and street"),
    PRIMARY_APPLICANT_ADDRESS_POSTCODE("primaryApplicantAddressPostCode", "Primary applicant postcode"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first name(s)"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_ADDRESS_LINE1("deceasedAddressLine1", "Deceased building and street"),
    DECEASED_ADDRESS_POSTCODE("deceasedAddressPostCode", "Deceased postcode"),
    DECEASED_DOB("deceasedDateOfBirth", "What was their date of birth?"),
    DECEASED_DOD("deceasedDateOfDeath", "What was their date of death?"),
    DECEASED_ASSETS_ANY_OTHER_NAMES("deceasedAnyOtherNames", "Did the deceased have assets in any other names?"),
    DECEASED_DOMICILE_IN_ENG_WALES("deceasedDomicileInEngWales",
            "Was the deceased domiciled in England or Wales at the time of their death?"),
    IHT_FORM_COMPLETED_ONLINE("ihtFormCompletedOnline", "IHT Form completed Online?"),
    IHT_GROSS_VALUE("ihtGrossValue", "Enter the gross value of the estate"),
    IHT_NET_VALUE("ihtNetValue", "Enter the net value of the estate");

    /*
    APPLCIANT
        { "metadata_field_name": "primaryApplicantHasAlias", "metadata_field_value": "true" },
        { "metadata_field_name": "primaryApplicantAlias", "metadata_field_value": "Jess Simpson" },

        APPLICANT
            - is the name different on the will?
            - if they are not applying, why are they not applying?
            - are more executors (I think this should be picked up from the application itself and we can
              put this in the script like reason for death)

        deceasedDomicileInEngWales
        deceasedAnyOtherNames
    */

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
