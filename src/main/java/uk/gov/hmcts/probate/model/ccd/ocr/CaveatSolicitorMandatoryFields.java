package uk.gov.hmcts.probate.model.ccd.ocr;

public enum CaveatSolicitorMandatoryFields {
    LEGAL_REPRESENTATIVE("legalRepresentative","Legal representative");
    
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
