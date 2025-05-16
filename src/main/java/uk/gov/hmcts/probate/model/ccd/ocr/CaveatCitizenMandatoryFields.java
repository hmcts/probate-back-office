package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatCitizenMandatoryFields {
    LEGAL_REPRESENTATIVE("legalRepresentative","Legal representative");

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
