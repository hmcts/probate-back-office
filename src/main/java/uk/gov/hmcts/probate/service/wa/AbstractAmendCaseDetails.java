package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@RequiredArgsConstructor
public class AbstractAmendCaseDetails {
    private final WaTaskService waTaskService;

    protected record Result(boolean caseHandedOffToLegacySite, boolean newHandOffPresent) {
    }

    protected Result getResult(CallbackRequest callbackRequest,
                             ResponseCaseData responseCaseData,
                             Predicate<CallbackRequest> predicate) {
        boolean caseHandedOffToLegacySite = Optional.ofNullable(callbackRequest.getCaseDetails()
                        .getData().getCaseHandedOffToLegacySite()).map(value -> value.equals(YES))
                .orElse(false);

        boolean newHandOffPresent = false;
        if (caseHandedOffToLegacySite) {
            setHandoffReasons(callbackRequest, responseCaseData, predicate);
            newHandOffPresent = !responseCaseData.getWaHandoffReasonList().isEmpty();
        }
        return new Result(caseHandedOffToLegacySite, newHandOffPresent);
    }

    private void setHandoffReasons(CallbackRequest callbackRequest,
                                   ResponseCaseData responseCaseData,
                                   Predicate<CallbackRequest> predicate) {
        Set<HandoffReason> handOffReasonBefore = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
        Set<HandoffReason> handOffReasonAfter = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetails());

        Set<HandoffReason> handOffReasons = new HashSet<>(handOffReasonAfter);

        if (predicate.test(callbackRequest)) {
            handOffReasons.removeAll(handOffReasonBefore);
        }

        List<CollectionMember<HandoffReason>> newHandOffReasons = handOffReasons.stream()
                .map(handoffReason -> new CollectionMember<>(UUID.randomUUID().toString(), handoffReason)
                ).toList();

        responseCaseData.setWaHandoffReasonList(newHandOffReasons);
    }
}
