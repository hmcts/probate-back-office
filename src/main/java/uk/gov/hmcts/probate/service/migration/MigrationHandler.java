package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;

public interface MigrationHandler<T> {
    T migrate(T callbackRequest, JSONObject migrationData);
}
