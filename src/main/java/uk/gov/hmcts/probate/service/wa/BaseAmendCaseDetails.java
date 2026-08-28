package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseAmendCaseDetails {

    private final WaTaskService waTaskService;

    protected void setHandOffReasons(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setWaHandOffReasonList(Collections.emptyList());

        if (waTaskService.getHandOffPredicate().test(callbackRequest)) {

            Set<HandoffReason> handOffReasonBefore = waTaskService
                    .getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
            Set<HandoffReason> handOffReasonAfter = waTaskService
                    .getGetHandOffReasons(callbackRequest.getCaseDetails());

            Set<HandoffReason> handOffReasons = new HashSet<>(handOffReasonAfter);
            handOffReasons.removeAll(handOffReasonBefore);

            List<CollectionMember<HandoffReason>> newHandOffReasons = handOffReasons.stream()
                    .map(handoffReason -> new CollectionMember<>(UUID.randomUUID().toString(), handoffReason)
                    ).toList();

            responseCaseData.setWaHandOffReasonList(newHandOffReasons);
            log.info("New handOffReasons added: {}", responseCaseData.getWaHandOffReasonList());
        }
    }

    protected void setCreateTask(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setCreateTask(waTaskService.getCaseTypePredicate().test(callbackRequest)
                ? Constants.YES : Constants.NO);
        log.info("Create task set to: {}", responseCaseData.getCreateTask());
    }
}
