package uk.gov.hmcts.probate.service.migration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

@Component
public class Dtspb5005CaveatRollback implements CaveatMigrationHandler {
    @Override
    public CaveatCallbackRequest migrate(CaveatCallbackRequest callbackRequest, JSONObject migrationData) {
        final CaveatDetails caveatDetails = callbackRequest.getCaseDetails();
        final CaveatData caveatData = caveatDetails.getData();

        caveatData.setApplicantOrganisationPolicy(null);
        return callbackRequest;
    }
}
