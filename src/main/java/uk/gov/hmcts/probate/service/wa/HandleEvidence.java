package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleEvidence implements CreateTaskProcessor {

    public static final String HANDLE_EVIDENCE
            = "handleEvidence";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return HANDLE_EVIDENCE;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData,
                        final String clientContext) {
        log.info("Processing case id {} state {} with case type {}",
                callbackRequest.getCaseDetails().getId(),
                callbackRequest.getCaseDetails().getState(),
                callbackRequest.getCaseDetails().getData().getCaseType());

        waTaskService.createTaskIfRequired(getEventId(),
                callbackRequest,
                responseCaseData);
    }
}
