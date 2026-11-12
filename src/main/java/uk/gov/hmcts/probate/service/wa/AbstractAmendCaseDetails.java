package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@RequiredArgsConstructor
public class AbstractAmendCaseDetails {
    private final WaTaskService waTaskService;

    protected record Result(boolean caseHandedOffToLegacySite, boolean newHandOffPresent) {
    }

    protected void process(final String authToken,
                        final CallbackRequest callbackRequest,
                        final ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        Result result = getResult(callbackRequest,
                responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !caseTypeChanged
                        && !result.caseHandedOffToLegacySite()
                        && waTaskService.isAllHandOffsRemoved(callbackRequest);

        responseCaseData.setCreateTask(caseTypeChanged
                || result.newHandOffPresent()
                || isInitialCaseExaminationTaskRequired
                ? YES : NO);

        log.info("case id {}: caseTypeChanged {},  newHandOffPresent {}"
                        + " new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                result.newHandOffPresent(),
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }

    protected Result getResult(CallbackRequest callbackRequest,
                             ResponseCaseData responseCaseData) {
        boolean caseHandedOffToLegacySite = YES.equals(callbackRequest.getCaseDetails().getData()
                        .getCaseHandedOffToLegacySite());

        boolean newHandOffPresent = false;
        if (caseHandedOffToLegacySite) {
            setHandoffReasons(callbackRequest, responseCaseData);
            newHandOffPresent = !responseCaseData.getWaHandoffReasonList().isEmpty();
        }
        return new Result(caseHandedOffToLegacySite, newHandOffPresent);
    }

    private void setHandoffReasons(CallbackRequest callbackRequest,
                                   ResponseCaseData responseCaseData) {
        Set<HandoffReason> handOffReasonBefore = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
        Set<HandoffReason> handOffReasonAfter = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetails());

        Set<HandoffReason> handOffReasons = new HashSet<>(handOffReasonAfter);

        if (!callbackRequest.isStateChanged()) {
            handOffReasons.removeAll(handOffReasonBefore);
        }

        List<CollectionMember<HandoffReason>> newHandOffReasons = handOffReasons.stream()
                .map(handoffReason -> new CollectionMember<>(UUID.randomUUID().toString(), handoffReason)
                ).toList();

        responseCaseData.setWaHandoffReasonList(newHandOffReasons);
    }
}
