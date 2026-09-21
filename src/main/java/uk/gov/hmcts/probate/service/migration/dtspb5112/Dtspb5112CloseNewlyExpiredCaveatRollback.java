package uk.gov.hmcts.probate.service.migration.dtspb5112;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;

@Component
public class Dtspb5112CloseNewlyExpiredCaveatRollback implements CaveatMigrationHandler {

    private static final String ORIGINAL_STATE = "originalState";

    @Override
    public CaveatCallbackRequest migrate(CaveatCallbackRequest callbackRequest, JSONObject migrationData) {
        final CaveatDetails caveatDetails = callbackRequest.getCaseDetails();
        final String originalState = migrationData.getString(ORIGINAL_STATE);

        if (!Dtspb5112CaveatStates.LIVE_STATES.contains(originalState)) {
            throw new IllegalArgumentException("Invalid DTSPB-5112 originalState: " + originalState);
        }

        caveatDetails.setState(originalState);

        return callbackRequest;
    }
}
