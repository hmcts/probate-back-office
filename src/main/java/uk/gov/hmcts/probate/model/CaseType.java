package uk.gov.hmcts.probate.model;

public enum CaseType {
    GRANT_OF_REPRESENTATION("GrantOfRepresentation", "Grant of Representation"),
    CAVEAT("Caveat", "Caveat"),
    LEGACY("Legacy", "Legacy");

    private final String code;
    private final String name;

    CaseType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
