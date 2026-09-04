package uk.gov.hmcts.probate.service.wa;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class WaTaskService {

    public Predicate<CallbackRequest> getCaseTypePredicate() {
        return callbackRequest ->
                !callbackRequest.getCaseDetails().getData().getCaseType()
                        .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType());
    }

    public Predicate<CallbackRequest> getHandOffPredicate() {
        return callbackRequest -> {
            Set<HandoffReason> handOffReasonBefore = getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
            Set<HandoffReason> handOffReasonAfter = getGetHandOffReasons(callbackRequest.getCaseDetails());

            if (handOffReasonBefore.isEmpty() && handOffReasonAfter.isEmpty()) {
                return true;
            }

            return !handOffReasonBefore.equals(handOffReasonAfter);
        };
    }

    public Set<HandoffReason> getGetHandOffReasons(CaseDetails caseDetails) {
        return ofNullable(caseDetails.getData())
                .map(CaseData::getBoHandoffReasonList)
                .stream()
                .flatMap(List::stream)
                .map(CollectionMember::getValue)
                .collect(Collectors.toSet());
    }
}
