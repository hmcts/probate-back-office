package uk.gov.hmcts.probate.model.probateman;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;

@RequiredArgsConstructor
public enum ProbateManType {

    CAVEAT(CcdCaseType.CAVEAT, EventId.IMPORT_CAVEAT),
    GRANT_APPLICATION(CcdCaseType.GRANT_OF_REPRESENTATION, EventId.IMPORT_GOR_CASE),
    STANDING_SEARCH(CcdCaseType.STANDING_SEARCH, EventId.IMPORT_STANDING_SEARCH),
    WILL_LODGEMENT(CcdCaseType.WILL_LODGEMENT, EventId.IMPORT_WILL_LODGEMENT);

    @Getter
    private final CcdCaseType ccdCaseType;

    @Getter
    private final EventId caseCreationEventId;
}
