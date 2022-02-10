package uk.gov.hmcts.probate.model.ccd.ocr;

import java.util.Arrays;

public enum IntestacyCitizenMandatoryFields {
    PRIMARY_APPLICANT_FORENAMES("primaryApplicantForenames", "Primary applicant first names"),
    PRIMARY_APPLICANT_SURNAME("primaryApplicantSurname", "Primary applicant last name"),
    PRIMARY_APPLICANT_ADDRESS_LINE1("primaryApplicantAddressLine1", "Primary applicant address line 1"),
    PRIMARY_APPLICANT_ADDRESS_POSTCODE("primaryApplicantAddressPostCode", "Primary applicant postcode"),
    DECEASED_FORENAMES("deceasedForenames", "Deceased first names"),
    DECEASED_SURNAME("deceasedSurname", "Deceased last name"),
    DECEASED_ADDRESS_LINE1("deceasedAddressLine1", "Deceased address line 1"),
    DECEASED_ADDRESS_POSTCODE("deceasedAddressPostCode", "Deceased postcode"),
    DECEASED_DOB("deceasedDateOfBirth", "Deceased date of birth"),
    DECEASED_DOD("deceasedDateOfDeath", "Deceased date of death"),
    DECEASED_ANY_OTHER_NAMES("deceasedAnyOtherNames", "Did the deceased have assets in any other names?"),
    DECEASED_DOMICILE_IN_ENG_WALES("deceasedDomicileInEngWales",
            "Was the deceased domiciled in England or Wales at the time of their death?"),
    IHT_FORM_COMPLETED_ONLINE("ihtFormCompletedOnline", "IHT form completed online?", "1"),
    IHT_GROSS_VALUE("ihtGrossValue", "Gross value of the estate"),
    IHT_NET_VALUE("ihtNetValue", "Net value of the estate"),
    SOLICITOR_APPLYING("solsSolicitorIsApplying","Do you have legal representative acting for you?"),
    IHT_400421_COMPLETED("iht400421completed", "Did you complete an IHT400 and IHT421 form?", "2"),
    IHT_IDENTIFIER("ihtReferenceNumber", "IHT identifier", "0"),
    IHT_ESTATE_GROSS("ihtEstateGrossValue", "gross value of the estate for inheritance tax", "0"),
    IHT_ESTATE_NET("ihtEstateNetValue", "net value of the estate for inheritance tax", "0"),
    IHT_ESTATE_NQV("ihtEstateNetQualifyingValue", "net qualifying value of the estate", "0"),
    DECEASED_LATE_SPOUSE("deceasedHadLateSpouseOrCivilPartner", "What was the marital status of the person who has " 
        + "died when they died? WIDOWED", "0"),
    IHT_UNUSED_ALLOWANCE("ihtUnusedAllowanceClaimed", "Are you claiming against this estate the unused proportion of " 
        + "the inheritance tax nil-rate band of a pre-deceased spouse or civil partner of the deceased?", "0")
        ;

    private final String key;
    private final String value;
    private final String[] formVersions; //1 or 2 or 0 if conditional

    IntestacyCitizenMandatoryFields(String key, String value) {
        this.key = key;
        this.value = value;
        this.formVersions = new String[]{"1", "2"};
    }

    IntestacyCitizenMandatoryFields(String key, String value, String... formVersions) {
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

    public boolean isVersion2() {
        return Arrays.stream(formVersions).anyMatch(r -> r.equals("2"));
    }
    
    public boolean isVersion1() {
        return Arrays.stream(formVersions).anyMatch(r -> r.equals("1"));
    }
}
