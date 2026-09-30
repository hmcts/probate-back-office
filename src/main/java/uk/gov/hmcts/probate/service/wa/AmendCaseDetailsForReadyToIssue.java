package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE;

@Slf4j
@RequiredArgsConstructor
@Component
public class AmendCaseDetailsForReadyToIssue  implements CreateTaskProcessor {

    private static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";
    private static final String BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE = "boAmendCaseDetailsForReadyToIssue";
    private final WaTaskService waTaskService;
    private static final List<TaskTypes> TASKS_AWAITING_DOCUMENTATION_TO_CLOSE = List.of(EXAMINE_DIGITAL_CASE_PROBATE,
            EXAMINE_DIGITAL_CASE_INTESTACY,
            EXAMINE_DIGITAL_CASE_ADMON_WILL,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);

    @Override
    public String getEventId() {
        return BO_AMEND_CASE_DETAILS_FOR_READY_TO_ISSUE;
    }

    @Override
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        boolean caseTypeChanged = waTaskService.getCaseTypePredicate().test(callbackRequest, null);

        boolean taskToClosePresent = !caseTypeChanged
                && waTaskService.isTaskPresent(authToken,
                callbackRequest.getCaseDetails().getId().toString(),
                BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                TASKS_AWAITING_DOCUMENTATION_TO_CLOSE);

        setHandoffReasons(callbackRequest, responseCaseData);

        boolean isInitialCaseExaminationTaskRequired =
                !waTaskService
                        .getGetHandOffReasons(callbackRequest.getCaseDetailsBefore())
                        .isEmpty()
                && waTaskService
                        .getGetHandOffReasons(callbackRequest.getCaseDetails())
                        .isEmpty();

        responseCaseData.setCreateTask(caseTypeChanged || taskToClosePresent
                || isInitialCaseExaminationTaskRequired
                ? Constants.YES : Constants.NO);

        log.info("case id {}: caseTypeChanged {}, taskToClosePresent {}, " +
                        "new handoffs {}, isInitialCaseExaminationTaskRequired {}",
                callbackRequest.getCaseDetails().getId(),
                caseTypeChanged,
                taskToClosePresent,
                responseCaseData.getWaHandoffReasonList(),
                isInitialCaseExaminationTaskRequired);
    }

    private void setHandoffReasons(CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        responseCaseData.setWaHandoffReasonList(Collections.emptyList());

        Set<HandoffReason> handOffReasonBefore = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
        Set<HandoffReason> handOffReasonAfter = waTaskService
                .getGetHandOffReasons(callbackRequest.getCaseDetails());

        Set<HandoffReason> handOffReasons = new HashSet<>(handOffReasonAfter);
        handOffReasons.removeAll(handOffReasonBefore);

        List<CollectionMember<HandoffReason>> newHandOffReasons = handOffReasons.stream()
                .map(handoffReason -> new CollectionMember<>(UUID.randomUUID().toString(), handoffReason)
                ).toList();

        responseCaseData.setWaHandoffReasonList(newHandOffReasons);
    }
}