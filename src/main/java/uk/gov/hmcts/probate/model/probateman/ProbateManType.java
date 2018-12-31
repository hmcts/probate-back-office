package uk.gov.hmcts.probate.model.probateman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;

@RequiredArgsConstructor
public enum ProbateManType {

    CAVEAT(CcdCaseType.CAVEAT, EventId.RAISE_CAVEAT),
    GRANT_APPLICATION(CcdCaseType.GRANT_OF_REPRESENTATION, EventId.APPLY_FOR_GRANT),
    STANDING_SEARCH(CcdCaseType.GRANT_OF_REPRESENTATION, EventId.CREATE_STANDING_SEARCH),
    WILL_LODGEMENT(CcdCaseType.WILL_LODGEMENT, EventId.CREATE_WILL_LODGEMENT);

    @Getter
    private final CcdCaseType ccdCaseType;

    @Getter
    private final EventId caseCreationEventId;
}
