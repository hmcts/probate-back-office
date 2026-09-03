package uk.gov.hmcts.probate.service.wa;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Component
public class AmendCaseDetailsForReadyToIssue implements CreateTaskProcessor {
    @Override
    public String getEventId() {
        return "boAmendCaseDetailsForReadyToIssue";
    }

    @Override
    public void process(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setCreateTask(callbackRequest.getCaseDetails().getData().getCaseType()
                .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType())
                ? Constants.NO : Constants.YES);
    }
}
