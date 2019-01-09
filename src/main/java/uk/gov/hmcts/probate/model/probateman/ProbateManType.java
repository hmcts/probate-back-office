package uk.gov.hmcts.probate.model.probateman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;

@RequiredArgsConstructor
public enum ProbateManType {

    CAVEAT(CcdCaseType.CAVEAT, EventId.RAISE_CAVEAT),
    GRANT_APPLICATION(CcdCaseType.GRANT_OF_REPRESENTATION, EventId.APPLY_FOR_GRANT),
    STANDING_SEARCH(CcdCaseType.STANDING_SEARCH, EventId.CREATE_STANDING_SEARCH),
    WILL_LODGEMENT(CcdCaseType.WILL_LODGEMENT, EventId.CREATE_WILL_LODGEMENT);

    @Getter
    private final CcdCaseType ccdCaseType;

    @Getter
    private final EventId caseCreationEventId;

    public static final ProbateManType getByLegacyCaseType(LegacyCaseType legacyCaseType) {
        if (LegacyCaseType.GRANT_OF_REPRESENTATION.getName().equals(legacyCaseType.getName())) {
            return ProbateManType.GRANT_APPLICATION;
        }
        if (LegacyCaseType.STANDING_SEARCH.getName().equals(legacyCaseType.getName())) {
            return ProbateManType.STANDING_SEARCH;
        }
        if (LegacyCaseType.WILL_LODGEMENT.getName().equals(legacyCaseType.getName())) {
            return ProbateManType.WILL_LODGEMENT;
        }
        if (LegacyCaseType.GRANT_OF_REPRESENTATION_DERIVED.getName().equals(legacyCaseType.getName())) {
            return ProbateManType.GRANT_APPLICATION;
        }

        return null;
    }
}
