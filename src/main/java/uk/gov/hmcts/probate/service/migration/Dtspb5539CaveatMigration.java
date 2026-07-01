package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;

@Component
public class Dtspb5539CaveatMigration implements CaveatMigrationHandler {
    @Override
    public CaveatCallbackRequest migrate(CaveatCallbackRequest callbackRequest, JSONObject migrationData) {
        return callbackRequest;
    }
}
