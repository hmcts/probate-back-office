package uk.gov.hmcts.probate.service.wa;

import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

public interface CreateTaskProcessor {
    String getEventId();

    void process(CallbackRequest callbackRequest, ResponseCaseData responseCaseData);
}
