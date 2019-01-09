package uk.gov.hmcts.probate.model.probateman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LegacyCaseType {

    CAVEAT("Legacy CAVEAT"),
    GRANT_OF_REPRESENTATION("Legacy LEGACY APPLICATION"),
    GRANT_OF_REPRESENTATION_DERIVED("Legacy LEGACY GRANT"),
    STANDING_SEARCH("Legacy STANDING SEARCH"),
    WILL_LODGEMENT("Legacy WILL");

    @Getter
    private final String name;

    public static final LegacyCaseType getByLegacyCaseTypeName(String legacyCaseTypeName) {
        if (GRANT_OF_REPRESENTATION.getName().equals(legacyCaseTypeName)) {
            return GRANT_OF_REPRESENTATION;
        }
        if (STANDING_SEARCH.getName().equals(legacyCaseTypeName)) {
            return STANDING_SEARCH;
        }
        if (WILL_LODGEMENT.getName().equals(legacyCaseTypeName)) {
            return WILL_LODGEMENT;
        }
        if (GRANT_OF_REPRESENTATION_DERIVED.getName().equals(legacyCaseTypeName)) {
            return GRANT_OF_REPRESENTATION_DERIVED;
        }

        return null;
    }

}
