package uk.gov.hmcts.probate.model.ccd.ocr;

import java.util.Arrays;

public enum GORSolicitorMandatoryFields {
    PRIMARY_APPLICANT_HAS_ALIAS("primaryApplicantHasAlias", "Primary applicant has alias?"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death"),
    FORM_VERSION("formVersion", "Form version"),
    IHT_GROSS_VALUE("ihtGrossValue", "Gross value of the estate", "1"),
    IHT_NET_VALUE("ihtNetValue", "Net value of the estate", "1");

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
