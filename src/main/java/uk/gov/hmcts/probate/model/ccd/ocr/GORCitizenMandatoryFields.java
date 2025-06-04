package uk.gov.hmcts.probate.model.ccd.ocr;

import java.util.Arrays;

public enum GORCitizenMandatoryFields {
    //PRIMARY_APPLICANT_HAS_ALIAS("primaryApplicantHasAlias", "Primary applicant has alias?"),
    //DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    //DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death"),
    EXCEPTED_ESTATE("exceptedEstate", "I did not have to submit any forms to HMRC.", "0"),
    FORM_VERSION("formVersion", "Form version"),
    IHT_GROSS_VALUE("ihtGrossValue", "Gross value of the estate", "1"),
    IHT_NET_VALUE("ihtNetValue", "Net value of the estate", "1"),
    SOLICITOR_APPLYING("solsSolicitorIsApplying","Do you have legal representative acting for you?"),
    IHT_400421_COMPLETED("iht400421completed", "Did you complete an IHT400 and IHT421 form?", "2"),
    IHT_207_COMPLETED("iht207completed", "IHT207", "0"),
    IHT_400_COMPLETED("iht400completed", "IHT400", "0"),
    IHT_205_COMPLETED("iht205completed", "IHT205", "0"),
    IHT_400_PROCESS("iht400process", "Have you received a letter from HMRC with your unique probate code?", "0"),
    IHT_CODE("ihtCode", "Free text box for HMRC code", "0"),
    IHT_GROSS_VALUE_EXCEPTED_ESTATE("ihtGrossValueExceptedEstate", "Gross value of the estate for probate", "0"),
    IHT_NET_VALUE_EXCEPTED_ESTATE("ihtNetValueExceptedEstate", "Net value of the estate for probate", "0"),
    PROBATE_GROSS_VALUE_IHT_400("probateGrossValueIht400", "The probate values from the HMRC letter Gross value", "0"),
    PROBATE_NET_VALUE_IHT_400("probateNetValueIht400", "The probate values from the HMRC letter net value", "0"),
    IHT_421_GROSS_VALUE("iht421grossValue", "Enter the gross value of the estate (IHT 400421)", "0"),
    IHT_421_NET_VALUE("iht421netValue", "Enter the net value of the estate (IHT 400421)", "0"),
    IHT_207_GROSS_VALUE("iht207grossValue", "Enter the gross value of the estate (IHT207 paper)", "0"),
    IHT_207_NET_VALUE("iht207netValue", "Enter the net value of the estate (IHT207 paper)", "0"),
    IHT_GROSS_VALUE_205("ihtGrossValue205", "Box A (gross value)", "0"),
    IHT_NET_VALUE_205("ihtNetValue205", "Box H (net value)", "0"),
    IHT_IDENTIFIER("ihtReferenceNumber", "IHT identifier", "0"),
    IHT_ESTATE_GROSS("ihtEstateGrossValue", "gross value of the estate for inheritance tax", "0"),
    IHT_ESTATE_NET("ihtEstateNetValue", "net value of the estate for inheritance tax", "0"),
    IHT_ESTATE_NQV("ihtEstateNetQualifyingValue", "net qualifying value of the estate", "0"),
    IHT_UNUSED_ALLOWANCE("ihtUnusedAllowanceClaimed", "Are you claiming against this estate the unused proportion of "
        + "the inheritance tax nil-rate band of a pre-deceased spouse or civil partner of the deceased?", "0")
        ;

    private final String key;
    private final String value;
    private final String[] formVersions; //1 or 2 or 0 if conditional

    GORCitizenMandatoryFields(String key, String value) {
        this.key = key;
        this.value = value;
        this.formVersions = new String[]{"1", "2", "3"};
    }

    GORCitizenMandatoryFields(String key, String value, String... formVersions) {
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
