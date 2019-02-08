package uk.gov.hmcts.probate.model.probateman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum LegacyCaseType {

    CAVEAT("Legacy CAVEAT", ProbateManType.CAVEAT),
    GRANT_OF_REPRESENTATION("Legacy LEGACY APPLICATION", ProbateManType.GRANT_APPLICATION),
    GRANT_OF_REPRESENTATION_DERIVED("Legacy LEGACY GRANT", ProbateManType.GRANT_APPLICATION),
    STANDING_SEARCH("Legacy STANDING SEARCH", ProbateManType.STANDING_SEARCH),
    WILL_LODGEMENT("Legacy WILL", ProbateManType.WILL_LODGEMENT);

    @Getter
    private final String name;

    @Getter
    private final ProbateManType probateManType;

    public static final LegacyCaseType getByLegacyCaseTypeName(String legacyCaseTypeName) {
        return Arrays.stream(LegacyCaseType.values())
            .filter(legacyCaseType -> legacyCaseType.getName().equals(legacyCaseTypeName))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
