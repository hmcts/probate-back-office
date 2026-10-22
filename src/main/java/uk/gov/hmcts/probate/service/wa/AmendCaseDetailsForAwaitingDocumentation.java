package uk.gov.hmcts.probate.service.wa;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Component
public class AmendCaseDetailsForAwaitingDocumentation implements CreateTaskProcessor {

    public static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION;
    }

    @Override
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setCreateTask(callbackRequest.getCaseDetails().getData().getCaseType()
                .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType())
                ? Constants.NO : Constants.YES);
    }
}
