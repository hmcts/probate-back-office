package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Slf4j
@Component
public class AmendCaseDetailsForReadyToIssue extends AbstractAmendCaseDetails implements CreateTaskProcessor {

    private static final String BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE = "boAmendCaseDetailsForReadyToIssue";

    public AmendCaseDetailsForReadyToIssue(WaTaskService waTaskService) {
        super(waTaskService);
    }

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData) {
        super.process(authToken, callbackRequest, responseCaseData);
    }
}
