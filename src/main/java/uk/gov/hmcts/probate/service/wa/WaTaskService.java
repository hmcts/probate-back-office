package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.StateConstants;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_HANDOFFS;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.getStateTasks;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaTaskService {
    private final SecurityUtils securityUtils;
    private final TaskUtils taskUtils;
    private final CcdClientApi ccdClientApi;

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

    public void closeReadyToIssueTasks(CallbackRequest callbackRequest,
                                       String clientContext) {
        triggerCloseHandOffEvent(callbackRequest, clientContext,
                handOffReason -> "Examine" + handOffReason);
        triggerCloseIceEvent(callbackRequest,
                clientContext,
                TaskTypes.examineReadyToIssueTasks());
    }


    public void closeAwaitingDocumentationTasks(CallbackRequest callbackRequest,
                                                String clientContext) {
        triggerCloseHandOffEvent(callbackRequest, clientContext,
                handOffReason -> "Examine" + handOffReason + STATE_CASE_PRINTED);
        triggerCloseIceEvent(callbackRequest,
                clientContext,
                TaskTypes.examineAwaitingDocTasks());
    }

    private void triggerCloseIceEvent(CallbackRequest callbackRequest,
                                      String clientContext,
                                      Set<TaskTypes> taskTypes) {
        Optional<TaskData> taskData = taskUtils.getTaskData(clientContext);
        Long caseId = callbackRequest.getCaseDetails().getId();
        Consumer<String> eventTrigger = caseType ->
                taskTypes.stream()
                        .filter(taskType -> Objects.nonNull(taskType.getCaseType()))
                        .filter(taskType -> taskType.getCaseType().equals(caseType))
                        .findFirst()
                        .flatMap(TaskTypes::getOptionalClosingEventId)
                        .ifPresent(eventId -> {
                            log.info("Case id {} : triggering closing event {}",
                                    caseId,
                                    eventId);
                            triggerEvent(callbackRequest.getCaseDetails(), eventId);
                        });

        if (isHandOffsAdded(callbackRequest)) {
            eventTrigger.accept(callbackRequest.getCaseDetails().getData().getCaseType());
        }

        if (taskData.isEmpty()) {
            boolean caseTypeChanged =
                    getCaseTypePredicate().test(callbackRequest, clientContext);

            if (caseTypeChanged) {

                String caseTypeBefore = callbackRequest.getCaseDetailsBefore().getData().getCaseType();

                log.info("Case id {} : no context task and remove case type as it has changed {}",
                        caseId,
                        caseTypeBefore);
                eventTrigger.accept(caseTypeBefore);
            }
        }
    }

    private void triggerCloseHandOffEvent(CallbackRequest callbackRequest,
                                          String clientContext,
                                          UnaryOperator<String> handOffReasonTask) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        if (isAllHandOffsRemoved(callbackRequest)) {
            triggerEvent(caseDetails, CLOSE_HANDOFFS);
        } else if (taskUtils.getTaskData(clientContext).isEmpty()) {

            Set<HandoffReason> handOffReasonBefore = getGetHandOffReasons(callbackRequest.getCaseDetailsBefore());
            Set<HandoffReason> handOffReasonAfter = getGetHandOffReasons(callbackRequest.getCaseDetails());

            Set<HandoffReason> handOffReasonsToBeRemoved = new HashSet<>(handOffReasonBefore);
            handOffReasonsToBeRemoved.removeAll(handOffReasonAfter);

            String caseState = callbackRequest.getCaseDetailsBefore().getState();

            BiConsumer<String, Map<String, TaskTypes>> eventTrigger = (state, stateTasks) -> {
                handOffReasonsToBeRemoved.stream()
                        .map(HandoffReason::getCaseHandoffReason)
                        .map(HandoffReasonId::getCode)
                        .map(handOffReasonTask)
                        .map(String::toUpperCase)
                        .forEach(handOffReasonTaskType -> {
                            log.info("Case id {} : handOffReasonTaskType to be removed {}",
                                    callbackRequest.getCaseDetails().getId(),
                                    handOffReasonTaskType);
                            triggerEvent(callbackRequest.getCaseDetails(),
                                    stateTasks.get(handOffReasonTaskType.toUpperCase())
                                            .getClosingEventId());
                        });

            };

            if (!handOffReasonsToBeRemoved.isEmpty()) {
                if (STATE_CASE_PRINTED.equals(caseState)) {
                    Map<String, TaskTypes> examineAwaitingDocTasks = getStateTasks(STATE_CASE_PRINTED);
                    eventTrigger.accept(STATE_CASE_PRINTED, examineAwaitingDocTasks);
                } else if (StateConstants.STATE_BO_READY_TO_ISSUE.equals(caseState)) {
                    Map<String, TaskTypes> examineReadyToIssueTask =
                            getStateTasks(StateConstants.STATE_BO_READY_TO_ISSUE);
                    eventTrigger.accept(StateConstants.STATE_BO_READY_TO_ISSUE, examineReadyToIssueTask);
                }
            }
        }
    }

    public boolean isAllHandOffsRemoved(final CallbackRequest callbackRequest) {
        return YES.equals(callbackRequest.getCaseDetailsBefore()
                .getData().getCaseHandedOffToLegacySite())
                && NO.equals(callbackRequest.getCaseDetails()
                .getData().getCaseHandedOffToLegacySite());
    }

    public boolean isHandOffsAdded(final CallbackRequest callbackRequest) {
        return NO.equals(callbackRequest.getCaseDetailsBefore()
                .getData().getCaseHandedOffToLegacySite())
                && YES.equals(callbackRequest.getCaseDetails()
                .getData().getCaseHandedOffToLegacySite());
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
