package uk.gov.hmcts.probate.model;

import java.util.Arrays;
import java.util.List;

public enum CaseType {
    GRANT_OF_REPRESENTATION("GrantOfRepresentation", "Grant of Representation"),
    CAVEAT("Caveat", "Caveat"),
    STANDING_SEARCH("StandingSearch", "Standing Search"),
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

    public static List<CaseType> getAll() {
        return Arrays.asList(GRANT_OF_REPRESENTATION, CAVEAT, STANDING_SEARCH, LEGACY);
    }
}
