package uk.gov.hmcts.probate.service.migration.dtspb5112;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;

@Component
public class Dtspb5112SetExpiryCaveatRollback implements CaveatMigrationHandler {

    @Override
    public CaveatCallbackRequest migrate(CaveatCallbackRequest callbackRequest, JSONObject migrationData) {
        final CaveatDetails caveatDetails = callbackRequest.getCaseDetails();
        final CaveatData caveatData = caveatDetails.getData();

        caveatData.setExpiryDate(null);

        return callbackRequest;
    }
}
