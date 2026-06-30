package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;

public class Dtspb5539GORMigration implements GorMigrationHandler {
    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest, JSONObject migrationData) {
        return callbackRequest;
    }
}
