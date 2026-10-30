package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ccd.EventId.AUTO_SELECT_FOR_QA_CREATE_TASK;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_HANDOFFS;
import static uk.gov.hmcts.probate.model.ccd.EventId.CLOSE_ICE_INTESTACY;


@ExtendWith(MockitoExtension.class)
class WaTaskServiceTest {

    public static final String CLIENT_CONTEXT = "client_context";
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

    private final String systemUserAuthToken = "system_user_authToken";
    private final String caseId = "123456";

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
    void shouldNotCloseReadyToIssueHandOffsWhenCaseIsHandedOffToLegacySiteIsRetained() {
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType("gop")
                        .caseHandedOffToLegacySite(Constants.YES)
                        .build());
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType("gop")
                        .caseHandedOffToLegacySite(Constants.YES)
                        .build());
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        waTaskService.closeReadyToIssueTasks(callbackRequest, CLIENT_CONTEXT);

        verifyNoInteractions(ccdClientApi);
    }

    @Test
    void shouldNotCloseReadyToIssueHandOffsWhenCaseIsHandedOffToLegacySiteIsAdded() {
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType("gop")
                        .caseHandedOffToLegacySite(NO)
                        .build());
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        CaseData caseData = CaseData.builder()
                .caseType("gop")
                .caseHandedOffToLegacySite(YES)
                .build();

        when(caseDetails.getData())
                .thenReturn(caseData);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);


        waTaskService.closeReadyToIssueTasks(callbackRequest, CLIENT_CONTEXT);

        verifyNoInteractions(ccdClientApi);
    }


    @Test
    void shouldCloseReadyToIssueHandOffsWhenCaseIsNotHandedOffToLegacySite() {
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType("gop")
                        .caseHandedOffToLegacySite(YES)
                        .build());
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        CaseData caseData = CaseData.builder()
                .caseType("gop")
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

        waTaskService.closeReadyToIssueTasks(callbackRequest, CLIENT_CONTEXT);

        verifyIceEvent(CLOSE_HANDOFFS);
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

    @Test
    void shouldCreateAndSubmitTaskForSystemUserSuccessfully() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(12345L);
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(SecurityDTO.builder().build());

        waTaskService.createAndSubmitTaskForSystemUser(
                callbackRequest,
                AUTO_SELECT_FOR_QA_CREATE_TASK
        );

        verify(ccdClientApi).triggerEvent(
                eq(String.valueOf(caseDetails.getId())),
                eq(AUTO_SELECT_FOR_QA_CREATE_TASK),
                functionCaptor.capture(),
                isA(SecurityDTO.class)
        );
        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId(AUTO_SELECT_FOR_QA_CREATE_TASK.getName())
                .token("event-token")
                .caseDetails(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.builder()
                        .data(Map.of())
                        .build())
                .build();

        CaseDataContent result =
                functionCaptor.getValue().apply(startEventResponse);

        assertThat(result.getEvent().getId())
                .isEqualTo(AUTO_SELECT_FOR_QA_CREATE_TASK.getName());

        assertThat(result.getEventToken())
                .isEqualTo("event-token");

        assertThat(result.getData()).isNotNull();
    }

    @ParameterizedTest(
            name = "[{index}] State: {0}, Evidence Handled: {1}, Expected: {2}"
    )
    @MethodSource("evidenceHandledScenarios")
    void shouldEvaluateEvidenceHandledPredicate(
            String state,
            String evidenceHandled,
            boolean expected) {


        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getState())
                .thenReturn(state);
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .evidenceHandled(evidenceHandled)
                        .build());

        boolean result = waTaskService
                .getEvidenceHandledPredicate()
                .test(callbackRequest, null);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> evidenceHandledScenarios() {
        return Stream.of(
                Arguments.of(STATE_BO_CASE_STOPPED, YES, true),
                Arguments.of(STATE_BO_CASE_STOPPED, NO, false),
                Arguments.of(STATE_BO_CASE_STOPPED, null, false),

                Arguments.of(STATE_BO_CASE_WORKER_ESCALATION, NO, true),
                Arguments.of(STATE_BO_CASE_WORKER_ESCALATION, YES, false),
                Arguments.of(STATE_BO_CASE_WORKER_ESCALATION, null, false),

                Arguments.of(STATE_BO_REDEC_NOTIFICATION_SENT, YES, true),
                Arguments.of(STATE_BO_REDEC_NOTIFICATION_SENT, NO, false),
                Arguments.of(STATE_BO_REDEC_NOTIFICATION_SENT, null, false),

                Arguments.of(STATE_CASE_PRINTED, YES, true),
                Arguments.of(STATE_CASE_PRINTED, NO, true),
                Arguments.of(STATE_CASE_PRINTED, null, true)
        );
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

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);
        verifyIceEvent(CLOSE_ICE_INTESTACY);
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
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseHandedOffToLegacySite(NO)
                        .build());
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        TaskData taskData = TaskData.builder()
                .type("ExamineDoubleProbate")
                .build();

        when(taskUtils.getTaskData(CLIENT_CONTEXT))
                .thenReturn(Optional.of(taskData));

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verify(ccdClientApi, never())
                .triggerEvent(any(), any(), any(), any());
    }

    @Test
    void shouldCloseAwaitingDocumentationHandOffsWhenCaseIsNotHandedOffToLegacySite() {
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseHandedOffToLegacySite(YES)
                        .build());
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

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

        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(SecurityDTO.builder().build());

        waTaskService.closeAwaitingDocumentationTasks(callbackRequest, CLIENT_CONTEXT);

        verifyIceEvent(CLOSE_HANDOFFS);
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