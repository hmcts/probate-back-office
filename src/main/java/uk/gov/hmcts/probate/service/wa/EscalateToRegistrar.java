package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

@Slf4j
@Component
@RequiredArgsConstructor
public class EscalateToRegistrar implements CreateTaskProcessor {

    public static final String BO_ESCALATE_TO_REGISTRAR
            = "boEscalateToRegistrar";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return BO_ESCALATE_TO_REGISTRAR;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData,
                        final String clientContext) {
        log.info("Processing escalate to registrar event for case id {}", callbackRequest.getCaseDetails().getId());
        waTaskService.removeTaskFromWaTaskPresent(callbackRequest, responseCaseData, clientContext);
    }
}
