package uk.gov.hmcts.probate.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.Constants.CAVEAT_NAME;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.Constants.GRANT_OF_REPRESENTATION_NAME;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.Constants.STANDING_SEARCH_NAME;
import static uk.gov.hmcts.probate.model.ccd.CcdCaseType.Constants.WILL_LODGEMENT_NAME;


@RequiredArgsConstructor
public enum CcdCaseType {

    @JsonProperty(GRANT_OF_REPRESENTATION_NAME) GRANT_OF_REPRESENTATION(GRANT_OF_REPRESENTATION_NAME),
    @JsonProperty(CAVEAT_NAME) CAVEAT(CAVEAT_NAME),
    @JsonProperty(STANDING_SEARCH_NAME) STANDING_SEARCH(STANDING_SEARCH_NAME),
    @JsonProperty(WILL_LODGEMENT_NAME) WILL_LODGEMENT(WILL_LODGEMENT_NAME);

    @Getter
    private final String name;

    public static class Constants {

        public static final String GRANT_OF_REPRESENTATION_NAME = "GrantOfRepresentation";

        public static final String CAVEAT_NAME = "Caveat";

        public static final String STANDING_SEARCH_NAME = "StandingSearch";

        public static final String WILL_LODGEMENT_NAME = "WillLodgement";

        private Constants() {
        }
    }
}


