package uk.gov.hmcts.probate.service.wa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Slf4j

@Component
public class AmendCaseDetailsForReadyToIssue extends BaseAmendCaseDetails implements CreateTaskProcessor {

    public AmendCaseDetailsForReadyToIssue(WaTaskService waTaskService) {
        super(waTaskService);
    }

    @Override
    public String getEventId() {
        return "boAmendCaseDetailsForReadyToIssue";
    }

    @Override
    public void process(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        setCreateTask(callbackRequest, responseCaseData);
        setHandoffReasons(callbackRequest, responseCaseData);
    }
}
