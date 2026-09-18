package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

@Component
public class Dtspb5005GorRollback implements GorMigrationHandler {
    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest, JSONObject migrationData) {
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();
        final CaseData caseData = caseDetails.getData();

        // NOTE when we remove this migration we should remove the method we call here from the CaseDataParent
        caseData.clearApplicantOrganisationPolicy(caseDetails.getId());
        return callbackRequest;
    }
}
