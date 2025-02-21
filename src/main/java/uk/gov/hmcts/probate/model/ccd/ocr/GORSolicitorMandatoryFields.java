package uk.gov.hmcts.probate.model.ccd.ocr;

import java.util.Arrays;

public enum GORSolicitorMandatoryFields {
    PRIMARY_APPLICANT_FORENAMES("primaryApplicantForenames", "Primary applicant first names"),
    PRIMARY_APPLICANT_SURNAME("primaryApplicantSurname", "Primary applicant last name"),
    PRIMARY_APPLICANT_ADDRESS_LINE1("primaryApplicantAddressLine1", "Primary applicant address line 1"),
    PRIMARY_APPLICANT_ADDRESS_POSTCODE("primaryApplicantAddressPostCode", "Primary applicant postcode"),
    PRIMARY_APPLICANT_HAS_ALIAS("primaryApplicantHasAlias", "Primary applicant has alias?"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_ADDRESS_LINE1("deceasedAddressLine1", "Deceased address line 1"),
    DECEASED_DOB("deceasedDateOfBirth", "Deceased date of birth"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death"),
    DECEASED_ANY_OTHER_NAMES("deceasedAnyOtherNames", "Did the deceased have assets in any other names?"),
    DECEASED_DOMICILE_IN_ENG_WALES("deceasedDomicileInEngWales",
            "Was the deceased domiciled in England or Wales at the time of their death?"),
    SOLICITOR_APPLYING("solsSolicitorIsApplying","Do you have legal representative acting for you?"),
    SOLICITOR_REPRESENTATIVE_NAME("solsSolicitorRepresentativeName", "Solicitor representative name"),
    SOLICITOR_FIRM_NAME("solsSolicitorFirmName", "Solicitors Firm name"),
    SOLICITOR_APP_REFERENCE("solsSolicitorAppReference", "Solictor application reference"),
    SOLICITOR_EMAIL_ADDRESS("solsSolicitorEmail", "Solictor email address"),
    FORM_VERSION("formVersion", "Form version"),
    IHT_GROSS_VALUE("ihtGrossValue", "Gross value of the estate", "1", "2"),
    IHT_NET_VALUE("ihtNetValue", "Net value of the estate", "1", "2");

    private final String key;
    private final String value;

    private final String[] formVersions;

    GORSolicitorMandatoryFields(String key, String value) {
        this.key = key;
        this.value = value;
        this.formVersions = new String[]{"1", "2", "3"};
    }

    GORSolicitorMandatoryFields(String key, String value, String... formVersions) {
        this.key = key;
        this.value = value;
        this.formVersions = formVersions;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


    public boolean isVersion3() {
        return Arrays.stream(formVersions).anyMatch(r -> r.equals("3"));
    }

    public boolean isVersion2() {
        return Arrays.stream(formVersions).anyMatch(r -> r.equals("2"));
    }

    public boolean isVersion1() {
        return Arrays.stream(formVersions).anyMatch(r -> r.equals("1"));
    }
}
