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
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
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
    @Mock
    private SecurityDTO securityDTO;
    @Mock
    private IdamApi idamApi;
    @Captor
    ArgumentCaptor<Function<StartEventResponse, CaseDataContent>> functionCaptor;

    @InjectMocks
    private WaTaskService waTaskService;

    @Test
    void shouldReturnTrueWhenTaskIsPresent() {
        TaskData task = new TaskData();
        task.setType("ExamineDigitalCaseProbate");

        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                List.of(task));

        String authToken = "auth-token";
        String caseId = "123456";
        String serviceToken = "service-token";

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
                List.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
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


        when(securityUtils.generateServiceToken()).thenReturn("service-token");
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                "auth-token",
                "123456",
                EVENT_TO_MONITOR,
                List.of(TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenResponseBodyIsNull() {
        when(securityUtils.generateServiceToken()).thenReturn("service-token");
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(null));

        boolean result = waTaskService.isTaskPresent(
                "auth-token",
                "123456",
                EVENT_TO_MONITOR,
                List.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTasksListIsEmpty() {
        GetTasksCompletableResponse<TaskData> response = new GetTasksCompletableResponse<>(
                false,
                Collections.emptyList());


        when(securityUtils.generateServiceToken()).thenReturn("service-token");
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                "auth-token",
                "123456",
                EVENT_TO_MONITOR,
                List.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
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

        when(securityUtils.generateServiceToken()).thenReturn("service-token");
        when(waApi.searchWithCriteriaForAutomaticCompletion(
                anyString(),
                anyString(),
                any(SearchEventAndCase.class)))
                .thenReturn(ResponseEntity.ok(response));

        boolean result = waTaskService.isTaskPresent(
                "auth-token",
                "123456",
                EVENT_TO_MONITOR,
                List.of(TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCaseTypeHasChanged() {
        setUpCallbackRequestForCaseType(
                "GrantOfProbate",
                "GrantOfAdministration"
        );

        BiPredicate<CallbackRequest, String> predicate =
                waTaskService.getCaseTypePredicate();

        assertThat(predicate.test(callbackRequest, ""))
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenCaseTypeHasNotChanged() {
        setUpCallbackRequestForCaseType(
                "GrantOfProbate",
                "GrantOfProbate"
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
                                        List.of(HandoffReasonId.FOREIGN_DOMICILE))
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
                                        List.of(HandoffReasonId.DOUBLE_PROBATE))
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
                        .boHandoffReasonList(generateHandOffReasonCollection(List.of(HandoffReasonId.DOUBLE_PROBATE,
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
                                        List.of(HandoffReasonId.DOUBLE_PROBATE,
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
        Long caseId = 123456789L;
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(NO)
                .build();

        when(caseDetails.getId())
                .thenReturn(caseId);
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(securityUtils.getAuthorisation())
                .thenReturn("auth-token");
        when(idamApi.retrieveUserInfo(anyString()))
                .thenReturn(UserInfo.builder()
                        .uid("user-id")
                        .build());

        waTaskService.closeReadyToIssueHandOffs(callbackRequest);

        verify(ccdClientApi).triggerEvent(
                eq(caseId.toString()),
                eq(CLOSE_READY_TO_ISSUE_HANDOFFS),
                functionCaptor.capture(),
                isA(SecurityDTO.class)
        );
        verify(idamApi).retrieveUserInfo(anyString());
        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId(CLOSE_READY_TO_ISSUE_HANDOFFS.getName())
                .token("event-token")
                .caseDetails(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.builder()
                        .data(Map.of())
                        .build())
                .build();

        CaseDataContent result =
                functionCaptor.getValue().apply(startEventResponse);

        assertThat(result.getEvent().getId())
                .isEqualTo(CLOSE_READY_TO_ISSUE_HANDOFFS.getName());

        assertThat(result.getEventToken())
                .isEqualTo("event-token");

        assertThat(result.getData()).isNotNull();
    }

    @Test
    void shouldBuildCorrectCaseDataContentWhenClosingReadyToIssueHandOffs() {

        Long caseId = 123456789L;
        CaseData caseData = CaseData.builder()
                .caseHandedOffToLegacySite(NO)
                .build();

        when(caseDetails.getId())
                .thenReturn(caseId);
        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(securityUtils.getAuthorisation())
                .thenReturn("auth-token");
        when(idamApi.retrieveUserInfo(anyString()))
                .thenReturn(UserInfo.builder()
                        .uid("user-id")
                        .build());

        waTaskService.closeReadyToIssueHandOffs(callbackRequest);

        verify(ccdClientApi).triggerEvent(
                eq(caseId.toString()),
                eq(CLOSE_READY_TO_ISSUE_HANDOFFS),
                functionCaptor.capture(),
                isA(SecurityDTO.class));

        verify(idamApi).retrieveUserInfo(anyString());

        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId(CLOSE_READY_TO_ISSUE_HANDOFFS.getName())
                .token("event-token")
                .caseDetails(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.builder()
                        .data(Map.of())
                        .build())
                .build();

        CaseDataContent result =
                functionCaptor.getValue().apply(startEventResponse);

        assertThat(result.getEvent().getId())
                .isEqualTo(CLOSE_READY_TO_ISSUE_HANDOFFS.getName());

        assertThat(result.getEventToken())
                .isEqualTo("event-token");

        assertThat(result.getData()).isNotNull();
    }

    private void setUpCallbackRequestForCaseType(
            String caseType,
            String caseTypeBefore) {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder().caseType(caseType).build());
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder().caseType(caseTypeBefore).build());
    }

    private List<CollectionMember<HandoffReason>> generateHandOffReasonCollection(
            List<HandoffReasonId> handOffReasons) {
        return handOffReasons.stream()
                .map(handoffReason ->
                        new CollectionMember<>(
                                UUID.randomUUID().toString(),
                                HandoffReason.builder().caseHandoffReason(handoffReason).build())
                ).toList();
    }
}