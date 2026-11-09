package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResolveCWEscalation implements CreateTaskProcessor {

    public static final String RESOLVE_CW_ESCALATION
            = "resolveCWEscalation";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return RESOLVE_CW_ESCALATION;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData,
                        final String clientContext) {
        log.info("Processing resolveCWEscalation event for case id {}", callbackRequest.getCaseDetails().getId());
        waTaskService.removeTaskFromWaTaskPresent(callbackRequest, responseCaseData, clientContext);
    }
}
