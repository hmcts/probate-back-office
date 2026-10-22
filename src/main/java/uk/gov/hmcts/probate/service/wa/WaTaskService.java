package uk.gov.hmcts.probate.service.wa;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_HANDOFFS;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_READY_TO_ISSUE_HANDOFFS;
import static uk.gov.hmcts.probate.model.ccd.JurisdictionId.PROBATE;
import static uk.gov.hmcts.probate.service.wa.AmendCaseDetailsForAwaitingDocumentation.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaTaskService {
    private final WaApi waApi;
    private final SecurityUtils securityUtils;
    private final TaskUtils taskUtils;
    private final CcdClientApi ccdClientApi;

    public boolean isTaskPresent(String authToken,
                                 String caseId,
                                 String eventId,
                                 @NonNull Set<TaskTypes> taskNames) {
        GetTasksCompletableResponse<TaskData> taskResponse = getWAResponse(authToken, caseId, eventId);

        return Optional.ofNullable(taskResponse)
                .map(GetTasksCompletableResponse::tasks)
                .stream()
                .flatMap(List::stream)
                .map(TaskData::getType)
                .map(TaskTypes::fromValue)
                .flatMap(Optional::stream)
                .anyMatch(taskNames::contains);
    }

    public Set<String> getAllAssociatedTasks(String authToken,
                                             String caseId,
                                             String eventId) {
        GetTasksCompletableResponse<TaskData> taskResponse = getWAResponse(authToken, caseId, eventId);

        return Optional.ofNullable(taskResponse)
                .map(GetTasksCompletableResponse::tasks)
                .stream()
                .flatMap(List::stream)
                .map(TaskData::getType)
                .collect(Collectors.toSet());
    }

    private GetTasksCompletableResponse<TaskData> getWAResponse(String authToken,
                                                                                String caseId,
                                                                                String eventId) {
        SearchEventAndCase searchEventAndCase = new SearchEventAndCase(
                caseId,
                eventId,
                PROBATE.name(),
                "grantofrepresentation"
        );

        ResponseEntity<GetTasksCompletableResponse<TaskData>> taskResponse =
                waApi.searchWithCriteriaForAutomaticCompletion(
                        authToken,
                        securityUtils.generateServiceToken(),
                        searchEventAndCase);

        log.info("WA task response status: {} for case id {}",
                taskResponse.getStatusCode(),
                caseId);
        return taskResponse.getBody();
    }

    public BiPredicate<CallbackRequest, String> getCaseTypePredicate() {
        return (callbackRequest, clientContext) ->
                !callbackRequest.getCaseDetails().getData().getCaseType()
                        .equals(callbackRequest.getCaseDetailsBefore().getData().getCaseType());
    }

    public BiPredicate<CallbackRequest, String> getHandOffPredicate() {
        return (callbackRequest, clientContext) -> {
            Set<HandoffReason> handOffReasonAfter = getGetHandOffReasons(callbackRequest.getCaseDetails());

            Optional<TaskData> taskData = taskUtils.getTaskData(clientContext);
            String currentWaTaskHandOff = taskData.map(TaskData::getType)
                    .orElse("");

            boolean caseHandedOffToLegacySite = Optional.ofNullable(callbackRequest.getCaseDetails()
                            .getData().getCaseHandedOffToLegacySite()).map(value -> value.equals(YES))
                    .orElse(false);

            if (caseHandedOffToLegacySite) {
                boolean isTaskHandOffReasonRetained = handOffReasonAfter.stream()
                        .map(HandoffReason::getCaseHandoffReason)
                        .map(HandoffReasonId::getCode)
                        .anyMatch(currentWaTaskHandOff::contains);

                log.info("Case id {} - current WA task  {}, isTaskHandOffReasonRetained: {}",
                        callbackRequest.getCaseDetails().getId(),
                        currentWaTaskHandOff,
                        isTaskHandOffReasonRetained);

                return !isTaskHandOffReasonRetained;
            }
            return true;
        };
    }

    public BiPredicate<CallbackRequest, String> getEvidenceHandledPredicate() {
        return (callbackRequest, clientContext) -> {

            log.debug("paramCallbackRequest.getCaseDetails().getState() {}", callbackRequest
                    .getCaseDetails().getState());
            String evidenceHandled = callbackRequest.getCaseDetails().getData()
                    .getEvidenceHandled();
            return switch (callbackRequest.getCaseDetails().getState()) {
                case STATE_BO_CASE_STOPPED, STATE_BO_REDEC_NOTIFICATION_SENT -> YES.equals(evidenceHandled);
                case STATE_BO_CASE_WORKER_ESCALATION -> NO.equals(evidenceHandled);
                case null, default -> true;
            };
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

    public void closeReadyToIssueHandOffs(CallbackRequest callbackRequest) {
        triggerCloseHandOffEvent(callbackRequest, CLOSE_READY_TO_ISSUE_HANDOFFS);
    }

    public void closeAwaitingDocumentationTasks(CallbackRequest callbackRequest,
                                                String clientContext) {
        triggerCloseHandOffEvent(callbackRequest, CLOSE_AWAITING_DOCUMENTATION_HANDOFFS);
        triggerCloseIceEvent(callbackRequest, clientContext);
    }

    private void triggerCloseIceEvent(CallbackRequest callbackRequest, String clientContext) {
        Optional<TaskData> taskData = taskUtils.getTaskData(clientContext);
        if (taskData.isEmpty() && getCaseTypePredicate().test(callbackRequest, clientContext)) {
            Long caseId = callbackRequest.getCaseDetails().getId();

            log.info("Case id {} : no context task and case type has changed",
                    caseId);

            getAllAssociatedTasks(securityUtils.getSecurityDTO().getAuthorisation(),
                    caseId.toString(),
                    BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION)
                    .stream()
                    .map(TaskTypes::fromValue)
                    .flatMap(Optional::stream)
                    .map(TaskTypes::getOptionalClosingEventId)
                    .flatMap(Optional::stream)
                    .forEach(eventId -> {
                        log.info("Case id {} : triggering closing event {}",
                                caseId,
                                eventId);
                        triggerEvent(callbackRequest.getCaseDetails(), eventId);
                    });
        }
    }

    private void triggerCloseHandOffEvent(CallbackRequest callbackRequest,
                                          EventId eventId) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        Optional.ofNullable(caseDetails.getData().getCaseHandedOffToLegacySite())
                .filter(NO::equals)
                .ifPresent(legacySite ->
                    triggerEvent(caseDetails, eventId));
    }

    private void triggerEvent(CaseDetails caseDetails, EventId eventId) {
        Function<StartEventResponse, CaseDataContent> caseDataContentFunction =
                startEventResponse ->
                        CaseDataContent.builder()
                                .event(Event.builder()
                                        .id(startEventResponse.getEventId())
                                        .build())
                                .eventToken(startEventResponse.getToken())
                                .data(startEventResponse.getCaseDetails().getData())
                                .build();

        ccdClientApi.triggerEvent(caseDetails.getId().toString(),
                eventId,
                caseDataContentFunction,
                securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO());
    }

    public void createAndSubmitTaskForSystemUser(CallbackRequest callbackRequest,
                                                 EventId eventId) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        Function<StartEventResponse, CaseDataContent> caseDataContentFunction =
                startEventResponse ->
                        CaseDataContent.builder()
                                .event(Event.builder()
                                        .id(startEventResponse.getEventId())
                                        .build())
                                .eventToken(startEventResponse.getToken())
                                .data(startEventResponse.getCaseDetails().getData())
                                .build();
        ccdClientApi.triggerEvent(caseDetails.getId().toString(),
                eventId,
                caseDataContentFunction,
                securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO());
    }
}
