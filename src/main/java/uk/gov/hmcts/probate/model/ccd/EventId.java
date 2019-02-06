package uk.gov.hmcts.probate.model.ccd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventId {

    APPLY_FOR_GRANT("applyForGrant"),
    RAISE_CAVEAT("raiseCaveat"),
    CREATE_STANDING_SEARCH("createStandingSearch"),
    CREATE_WILL_LODGEMENT("createWillLodgment");

    @Getter
    private final String name;

}
