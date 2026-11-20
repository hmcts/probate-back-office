package uk.gov.hmcts.probate.service.migration.dtspb5112;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;

import static uk.gov.hmcts.probate.service.migration.dtspb5112.Dtspb5112CaveatStates.CAVEAT_RAISED;


@Component
public class Dtspb5112PaAppCreatedCaveatMigration implements CaveatMigrationHandler {

    @Override
    public CaveatCallbackRequest migrate(CaveatCallbackRequest callbackRequest, JSONObject migrationData) {
        final CaveatDetails caveatDetails = callbackRequest.getCaseDetails();

        caveatDetails.setState(CAVEAT_RAISED);

        return callbackRequest;
    }
}
