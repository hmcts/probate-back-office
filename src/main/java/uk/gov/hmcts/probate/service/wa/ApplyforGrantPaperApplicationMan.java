package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyforGrantPaperApplicationMan implements CreateTaskProcessor {

    public static final String APPLY_FOR_GRANT_PAPER_APPLICATION_MAN
            = "applyforGrantPaperApplicationMan";

    private final WaTaskService waTaskService;

    @Override
    public String getEventId() {
        return APPLY_FOR_GRANT_PAPER_APPLICATION_MAN;
    }

    @Override
    public void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData,
                        final String clientContext) {
        String caseType = callbackRequest.getCaseDetails().getData().getCaseType();
        log.info("Processing case id {} with case type {}",
                callbackRequest.getCaseDetails().getId(),
                caseType);
        TaskTypes.getAwaitingDocIceTask(caseType)
                .map(TaskTypes::getValue)
                .ifPresent(taskName -> responseCaseData.getWaTasksPresent()
                        .add(new CollectionMember<>(UUID.randomUUID().toString(), taskName)));
    }
}
