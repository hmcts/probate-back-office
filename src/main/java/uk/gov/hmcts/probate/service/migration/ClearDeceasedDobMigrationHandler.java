package uk.gov.hmcts.probate.service.migration;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

@Slf4j
@Component
public class ClearDeceasedDobMigrationHandler implements GorMigrationHandler {
    @Override
    public CallbackRequest migrate(
            final CallbackRequest callbackRequest,
            final JSONObject migrationData) {
        final CaseDetails caseDetails = callbackRequest.getCaseDetails();

        final long caseId = caseDetails.getId();
        final CaseData caseData = caseDetails.getData();

        log.info("Migrating case {} to clear deceased date of birth", caseId);
        caseData.clearDeceasedDateOfBirth(caseId);

        return callbackRequest;
    }
}
