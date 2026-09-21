package uk.gov.hmcts.probate.service.migration.dtspb5112;

import java.util.Set;

public final class Dtspb5112CaveatStates {
    public static final String PA_APP_CREATED = "PAAppCreated";
    public static final String CAVEAT_RAISED = "CaveatRaised";
    public static final String CAVEAT_MATCHING = "CaveatMatching";
    public static final String AWAITING_CAVEAT_RESOLUTION = "AwaitingCaveatResolution";
    public static final String WARNING_VALIDATION = "WarningValidation";
    public static final String AWAITING_WARNING_RESPONSE = "AwaitingWarningResponse";
    public static final String CAVEAT_CLOSED = "CaveatClosed";

    public static final Set<String> LIVE_STATES = Set.of(
        CAVEAT_RAISED,
        CAVEAT_MATCHING,
        AWAITING_CAVEAT_RESOLUTION,
        WARNING_VALIDATION,
        AWAITING_WARNING_RESPONSE
    );

    private Dtspb5112CaveatStates() {
    }
}
