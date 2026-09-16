package uk.gov.hmcts.probate.service.migration;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.DataMigrationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

@Slf4j
@Component
public class Dtspb5113GorMigration implements GorMigrationHandler {
    private static final List<String> POST_GRANT_STATE_LIST = List.of(
            "BOPostGrantIssued",
            "BOExaminingReissue",
            "BOCaseMatchingReissue",
            "BOCaseStoppedReissue",
            "BOGrantIssuedRegistrarEscalation",
            "BOPostGrantIssuedRegistrarEscalation");

    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest, JSONObject migrationData) {
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        final String migrateToState = migrationData.getString("migrateToState");
        if (POST_GRANT_STATE_LIST.contains(migrateToState)) {
            log.info("Migrate case: {} to state: {}", caseDetails.getId(), migrateToState);
            caseDetails.setState(migrateToState);
        } else {
            final String errorMsg = "Invalid state for migration: " + migrateToState
                + " for case: " + caseDetails.getId();
            log.error(errorMsg);
            throw new DataMigrationException(errorMsg);
        }
        return callbackRequest;
    }
}
