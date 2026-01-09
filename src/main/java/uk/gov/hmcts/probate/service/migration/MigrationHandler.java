package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;

public interface MigrationHandler<CallbackRequestType> {
    CallbackRequestType migrate(CallbackRequestType callbackRequest, JSONObject migrationData);
}
