package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static uk.gov.hmcts.probate.model.StateConstants.STATE_DORMANT;

@Component
public class Dtspb5113GorRollback implements GorMigrationHandler {
    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest, JSONObject migrationData) {
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        caseDetails.setState(STATE_DORMANT);

        return callbackRequest;
    }
}
