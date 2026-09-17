package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@RequiredArgsConstructor
@Component
public class AmendCaseDetailsForAwaitingDocumentation implements CreateTaskProcessor {

    public static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION;
    }

    @Override
    public void process(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setCreateTask(waTaskService.getCaseTypePredicate().test(callbackRequest)
                ? Constants.YES : Constants.NO);
    }
}
