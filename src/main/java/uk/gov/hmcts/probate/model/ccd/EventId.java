package uk.gov.hmcts.probate.model.ccd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventId {

    IMPORT_GOR_CASE("boImportGrant"),
    IMPORT_CAVEAT("importCaveat"),
    IMPORT_STANDING_SEARCH("importSS"),
    IMPORT_WILL_LODGEMENT("importWill");

    @Getter
    private final String name;

}
