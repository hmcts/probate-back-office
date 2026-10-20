package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_HANDOFFS;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_INTESTACY;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_AWAITING_DOCUMENTATION_ICE_PROBATE;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_READY_TO_ISSUE_HANDOFFS;


@ExtendWith(MockitoExtension.class)
class WaTaskServiceTest {
    public static final String EVENT_TO_MONITOR = "boAmendCaseDetailsForAwaitingDocumentation";
    public static final String CLIENT_CONTEXT = "client_context";
    @Mock
    private WaApi waApi;
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CaseDetails caseDetailsBefore;
    @Mock
    private TaskUtils taskUtils;
    @Mock
    private CcdClientApi ccdClientApi;

    @Captor
    ArgumentCaptor<Function<StartEventResponse, CaseDataContent>> functionCaptor;

    @InjectMocks
    private WaTaskService waTaskService;

    private final String authToken = "auth-token";
    private final String systemUserAuthToken = "system_user_authToken";
    private final String caseId = "123456";
    private final String serviceToken = "service-token";

    @Test
    void shouldReturnTrueWhenTaskIsPresent() {
        TaskData task = new TaskData();
        task.setType("ExamineDigitalCaseProbate");

        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                List.of(task));

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                authToken,
                caseId,
                EVENT_TO_MONITOR,
                Set.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTaskIsNotPresent() {
        TaskData task = new TaskData();
        task.setType("ExamineDigitalCaseProbate");

        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                List.of(task));


        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                authToken,
                caseId,
                EVENT_TO_MONITOR,
                Set.of(TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenResponseBodyIsNull() {
        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(null));

        boolean result = waTaskService.isTaskPresent(
                authToken,
                caseId,
                EVENT_TO_MONITOR,
                Set.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTasksListIsEmpty() {
        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                Collections.emptyList());


        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                authToken,
                caseId,
                EVENT_TO_MONITOR,
                Set.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTaskTypeIsUnknown() {
        TaskData task = new TaskData();
        task.setType("UnknownTaskType");

        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                List.of(task)
        );

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                authToken,
                caseId,
                EVENT_TO_MONITOR,
                Set.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCaseTypeHasChanged() {
        setUpCallbackRequestForCaseType(
                "gop",
                "intestacy"
        );

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getCaseTypePredicate();

        assertThat(predicate.test(callbackRequest, ""))
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenCaseTypeHasNotChanged() {
        setUpCallbackRequestForCaseType(
                "gop",
                "gop"
        );

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getCaseTypePredicate();

        assertThat(predicate.test(callbackRequest, ""))
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenEvidenceHasIsNo() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .evidenceHandled(NO)
                        .build());

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getEvidenceHandledPredicate();

        assertThat(predicate.test(callbackRequest, ""))
                .isTrue();
    }

    @Test
    void shouldReturnTrueWhenEvidenceHasIsYes() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .evidenceHandled(YES)
                        .build());

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getEvidenceHandledPredicate();

        assertThat(predicate.test(callbackRequest, ""))
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenHandOffReasonsHaveChanged() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        Set.of(HandoffReasonId.FOREIGN_DOMICILE))
                        ).build());
        TaskData task = new TaskData();
        task.setType("ExamineDeBonisNon");

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(task));

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest, CLIENT_CONTEXT))
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenHandOffReasonsHaveNotChanged() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseHandedOffToLegacySite(YES)
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        Set.of(HandoffReasonId.DOUBLE_PROBATE))
                        ).build());

        TaskData task = new TaskData();
        task.setType("ExamineDoubleProbate");

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(task));

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest, CLIENT_CONTEXT))
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenNoHandOffReasonsPresent() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .build());
        TaskData task = new TaskData();
        task.setType("ExamineDoubleProbate");

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(task));

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest, CLIENT_CONTEXT))
                .isTrue();
    }

    @Test
    void shouldReturnHandOffReasonsFromCaseDetails() {
        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(generateHandOffReasonCollection(Set.of(HandoffReasonId.DOUBLE_PROBATE,
                                HandoffReasonId.FOREIGN_DOMICILE)))
                        .build());

        Set<HandoffReason> result =
                waTaskService.getGetHandOffReasons(caseDetails);

        assertThat(result)
                .extracting("caseHandoffReason")
                .containsExactlyInAnyOrder(
                        HandoffReasonId.DOUBLE_PROBATE,
                        HandoffReasonId.FOREIGN_DOMICILE);
    }

    @Test
    void shouldReturnEmptySetWhenHandOffReasonsAreNull() {
        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .build());

        Set<HandoffReason> result =
                waTaskService.getGetHandOffReasons(caseDetails);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnFalseWhenTaskHandOffReasonRetained() {
        TaskData task = new TaskData();
        task.setType("ExamineLeadingFollowing Grants");

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(task));

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseHandedOffToLegacySite(YES)
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        Set.of(HandoffReasonId.DOUBLE_PROBATE,
                                                HandoffReasonId.LEADING_FOLLOWING_GRANTS,
                                                HandoffReasonId.FOREIGN_DOMICILE))
                        ).build());

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest, CLIENT_CONTEXT))
                .isFalse();
    }

    @Test
    void shouldNotCloseReadyToIssueHandOffsWhenCaseIsHandedOffToLegacySite() {
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(Constants.YES)
                .build();
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        waTaskService.closeReadyToIssueHandOffs(callbackRequest);

        verifyNoInteractions(ccdClientApi);
    }

    @Test
    void shouldCloseReadyToIssueHandOffsWhenCaseIsNotHandedOffToLegacySite() {
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(NO)
                .build();

        when(caseDetails.getId())
                .thenReturn(Long.valueOf(caseId));
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(SecurityDTO.builder().build());

        waTaskService.closeReadyToIssueHandOffs(callbackRequest);

        verifyIceEvent(CLOSE_READY_TO_ISSUE_HANDOFFS);
    }

    @Test
    void shouldNotCloseAwaitingDocumentationHandOffsWhenCaseIsHandedOffToLegacySite() {
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(Constants.YES)
                .caseType("gop")
                .build();
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetailsBefore.getData())
                .thenReturn(caseData);

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verifyNoInteractions(ccdClientApi);
    }

    @Test
    void shouldNotCloseAwaitingDocumentationIceWhenCaseTypeHasNotChanged() {
        setUpCallbackRequestForCaseType(
                "gop",
                "gop"
        );
        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.empty());
        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verifyNoInteractions(waApi);
        verifyNoInteractions(ccdClientApi);
    }

    @Test
    void shouldTriggerClosingEventWhenNoContextTaskAndCaseTypeChanged() {
        setUpCallbackRequestForCaseType(
                "gop",
                "intestacy"
        );

        when(caseDetails.getId())
                .thenReturn(Long.valueOf(caseId));

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.empty());

        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(SecurityDTO.builder()
                        .authorisation(systemUserAuthToken)
                        .build());

        when(securityUtils.getSecurityDTO())
                .thenReturn(SecurityDTO.builder()
                        .authorisation(authToken)
                        .build());

        when(securityUtils.generateServiceToken())
                .thenReturn(serviceToken);

        TaskData task1 = TaskData.builder()
                .type("ExamineDigitalCaseProbate")
                .build();

        TaskData task2 = TaskData.builder()
                .type("ExamineDigitalCaseIntestacy")
                .build();

        GetTasksCompletableResponse<TaskData> response =
                new GetTasksCompletableResponse<>(
                        false,
                        List.of(task1, task2)
                );

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);
        verifyIceEvent(CLOSE_AWAITING_DOCUMENTATION_ICE_PROBATE);
        verifyIceEvent(CLOSE_AWAITING_DOCUMENTATION_ICE_INTESTACY);
    }

    private void verifyIceEvent(EventId eventId) {
        verify(ccdClientApi).triggerEvent(
                eq(caseId),
                eq(eventId),
                functionCaptor.capture(),
                isA(SecurityDTO.class)
        );

        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId(eventId.getName())
                .token("event-token")
                .caseDetails(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.builder()
                        .data(Map.of())
                        .build())
                .build();

        CaseDataContent result =
                functionCaptor.getValue().apply(startEventResponse);

        assertThat(result.getEvent().getId())
                .isEqualTo(eventId.getName());

        assertThat(result.getEventToken())
                .isEqualTo("event-token");

        assertThat(result.getData()).isNotNull();
    }

    @Test
    void shouldNotTriggerEventWhenContextTaskExists() {
        CaseData caseData = CaseData.builder()
               .caseType("gop")
               .build();
        when(caseDetails.getData())
               .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
               .thenReturn(caseDetails);

        TaskData taskData = TaskData.builder()
                .type("ExamineDoubleProbate")
                .build();

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(taskData));

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verify(waApi, never())
                .searchWithCriteriaForAutomaticCompletion(any(), any(), any());

        verify(ccdClientApi, never())
                .triggerEvent(any(), any(), any(), any());
    }

    @Test
    void shouldNotTriggerEventWhenNoAssociatedTasksFound() {
        setUpCallbackRequestForCaseType(
                "gop",
                "intestacy"
        );

        when(caseDetails.getId())
                .thenReturn(Long.valueOf(caseId));

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.empty());


        when(securityUtils.getSecurityDTO())
                .thenReturn(SecurityDTO.builder()
                        .authorisation(authToken)
                        .build());

        when(securityUtils.generateServiceToken())
                .thenReturn(serviceToken);

        GetTasksCompletableResponse<TaskData> response =
                new GetTasksCompletableResponse<>(
                        false,
                        List.of()
                );

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));


        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verifyNoInteractions(ccdClientApi);
    }

    @Test
    void shouldCloseAwaitingDocumentationHandOffsWhenCaseIsNotHandedOffToLegacySite() {
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(NO)
                .caseType("gop")
                .build();

        when(caseDetails.getId())
                .thenReturn(Long.valueOf(caseId));
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetailsBefore.getData())
                .thenReturn(caseData);
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(SecurityDTO.builder().build());

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verifyIceEvent(CLOSE_AWAITING_DOCUMENTATION_HANDOFFS);
    }

    @Test
    void shouldReturnAllAssociatedTasks() {

        String examineIbisCasePrinted = "ExamineIBISCasePrinted";
        TaskData task1 = TaskData.builder()
                .type(examineIbisCasePrinted)
                .build();

        String examineLostWillCasePrinted = "ExamineLostWillCasePrinted";
        TaskData task2 = TaskData.builder()
                .type(examineLostWillCasePrinted)
                .build();

        GetTasksCompletableResponse<TaskData> response =
                new GetTasksCompletableResponse<>(
                        false,
                        List.of(task1, task2)
                );

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                any(SearchEventAndCase.class)
        )).thenReturn(ResponseEntity.ok(response));

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);

        Set<String> result =
                waTaskService.getAllAssociatedTasks(authToken,
                        caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT);

        assertThat(result)
                .containsExactlyInAnyOrder(
                        examineIbisCasePrinted,
                        examineLostWillCasePrinted);

        verify(waApi).searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                isA(SearchEventAndCase.class)
        );
    }

    @Test
    void shouldReturnEmptySetWhenNoTasksFound() {

        GetTasksCompletableResponse<TaskData> response =
                new GetTasksCompletableResponse<>(
                        false,
                        List.of()
                );

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)
        )).thenReturn(ResponseEntity.ok(response));

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);

        Set<String> result =
                waTaskService.getAllAssociatedTasks(authToken,
                        caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptySetWhenResponseBodyIsNull() {

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)
        )).thenReturn(ResponseEntity.ok().build());

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);

        Set<String> result =
                waTaskService.getAllAssociatedTasks(authToken,
                        caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldRemoveDuplicateTaskTypes() {

        TaskData task1 = TaskData.builder()
                .type("examineDigitalCaseProbate")
                .build();

        TaskData task2 = TaskData.builder()
                .type("examineDigitalCaseProbate")
                .build();

        GetTasksCompletableResponse<TaskData> response =
                new GetTasksCompletableResponse<>(
                        false,
                        List.of(task1, task2)
                );

        when(waApi.searchWithCriteriaForAutomaticCompletion(
                eq(authToken),
                eq(serviceToken),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        when(securityUtils.generateServiceToken()).thenReturn(serviceToken);

        Set<String> result =
                waTaskService.getAllAssociatedTasks(authToken,
                        caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION_EVENT);

        assertThat(result)
                .containsExactly("examineDigitalCaseProbate");
    }

    private void setUpCallbackRequestForCaseType(
            String caseType,
            String caseTypeBefore) {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType(caseType)
                        .build());
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder().caseType(caseTypeBefore).build());
    }

    private List<CollectionMember<HandoffReason>> generateHandOffReasonCollection(
            Set<HandoffReasonId> handOffReasons) {
        return handOffReasons.stream()
                .map(handoffReason ->
                        new CollectionMember<>(
                                UUID.randomUUID().toString(),
                                HandoffReason.builder().caseHandoffReason(handoffReason).build())
                ).toList();
    }
}