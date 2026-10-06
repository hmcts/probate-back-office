package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.IdamApi;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.wa.WaApi;
import uk.gov.hmcts.probate.service.wa.WaTaskService;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId.DOUBLE_PROBATE;
import static uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId.INCAPACITY_RULE35;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class WaTaskContollerUnitTest {
    public static final String ENCODED_CLIENT_CONTEXT = "encodedClientContext";
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;
    @Mock
    private CaseData caseData;
    @Mock
    private CaseData caseDataBefore;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private TaskUtils taskUtils;
    @Mock
    private WorkAllocationToggleService workAllocationToggleService;
    @Mock
    private WaApi waApi;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CcdClientApi ccdClientApi;
    @Mock
    private IdamApi idamApi;

    private WaTaskService waTaskService;

    private WaTaskContoller waTaskContoller;

    @Captor
    private ArgumentCaptor<BiPredicate<CallbackRequest, String>> predicateArgumentCaptor;

    private final String clientContext = "clientContext";

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(waApi, securityUtils, taskUtils, ccdClientApi, idamApi));
        waTaskContoller = new WaTaskContoller(taskUtils, objectMapper, workAllocationToggleService, waTaskService);
    }

    @Test
    void shouldNotCompleteTheExistingTaskAndNoNewTaskCreatedForCaseType() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore()).thenReturn(caseDetailsBefore);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetailsBefore.getData()).thenReturn(caseDataBefore);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseDataBefore.getCaseType()).thenReturn("gop");
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(ENCODED_CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateCaseTypeClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(ENCODED_CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, ENCODED_CLIENT_CONTEXT)).isFalse();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldCompleteTheExistingTaskAndNewTaskCreatedForCaseType() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore()).thenReturn(caseDetailsBefore);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetailsBefore.getData()).thenReturn(caseDataBefore);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseDataBefore.getCaseType()).thenReturn("intestacy");
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(ENCODED_CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateCaseTypeClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(ENCODED_CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, ENCODED_CLIENT_CONTEXT)).isTrue();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldNotCompleteTheExistingTaskForHandOffReasonsWhenNewHandOffReasonAdded() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getCaseHandedOffToLegacySite())
                .thenReturn(YES);
        when(caseData.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(
                        DOUBLE_PROBATE,
                        INCAPACITY_RULE35)));

        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(ENCODED_CLIENT_CONTEXT));

        Optional<TaskData> examineIncapacityRule35 = Optional.of(TaskData.builder()
                .type("ExamineIncapacityRule35")
                .build());

        doReturn(examineIncapacityRule35)
                .when(taskUtils)
                .getTaskData(clientContext);

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateHandOffClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(ENCODED_CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, clientContext)).isFalse();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getHandOffPredicate();
    }

    @Test
    void shouldNotCompleteTheExistingTaskForHandOffs() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(DOUBLE_PROBATE)));

        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(ENCODED_CLIENT_CONTEXT));

        Optional<TaskData> examineIncapacityRule35 = Optional.of(TaskData.builder()
                .type("ExamineIncapacityRule35")
                .build());

        doReturn(examineIncapacityRule35)
                .when(taskUtils)
                .getTaskData(clientContext);

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateHandOffClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(ENCODED_CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, clientContext)).isTrue();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getHandOffPredicate();
    }

    @Test
    void shouldCompleteTheExistingTaskForHandOffReasons() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(DOUBLE_PROBATE)));
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(ENCODED_CLIENT_CONTEXT));

        Optional<TaskData> examineForeignDomicile = Optional.of(TaskData.builder()
                .type("ExamineForeignDomicile")
                .build());

        doReturn(examineForeignDomicile)
                .when(taskUtils)
                .getTaskData(clientContext);

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateHandOffClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(ENCODED_CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, clientContext)).isTrue();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getHandOffPredicate();
    }

    @Test
    void shouldByPassWaCompletionFlag() {
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(false);
        ResponseEntity<CallbackResponse> response = waTaskContoller.updateCaseTypeClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldByPassCloseReadyToIssueHandOffs() {
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(false);
        waTaskContoller.closeReadyToIssueHandOffs(
                callbackRequest,
                bindingResult,
                httpServletRequest);

        verify(waTaskService, never()).closeReadyToIssueHandOffs(callbackRequest);
    }

    @Test
    void shouldInvokeCloseReadyToIssueHandOffs() {
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        doNothing().when(waTaskService).closeReadyToIssueHandOffs(callbackRequest);

        ResponseEntity<CallbackResponse> callbackResponseResponseEntity = waTaskContoller.closeReadyToIssueHandOffs(
                callbackRequest,
                bindingResult,
                httpServletRequest);
        assertThat(callbackResponseResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        verify(waTaskService).closeReadyToIssueHandOffs(callbackRequest);
    }


    @Test
    void shouldThrowBadRequestExceptionWhenBindingResultHasErrors() {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        assertThatThrownBy(() ->
                waTaskContoller.updateCaseTypeClientContext(
                        callbackRequest,
                        null,
                        bindingResult,
                        httpServletRequest
                )
        ).isInstanceOf(BadRequestException.class);

        verifyNoInteractions(taskUtils);
    }

    @Test
    void shouldContinueWhenObjectMapperFailsToLogRequest() throws Exception {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(objectMapper.writeValueAsString(callbackRequest))
                .thenThrow(new JsonProcessingException("Unable to serialize") {});

        when(taskUtils.setTaskCompletion(
                any(),
                eq(callbackRequest),
                any()
        )).thenReturn(Optional.empty());
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        ResponseEntity<CallbackResponse> response =
                waTaskContoller.updateCaseTypeClientContext(
                        callbackRequest,
                        null,
                        bindingResult,
                        httpServletRequest
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(taskUtils).setTaskCompletion(
                any(),
                eq(callbackRequest),
                any()
        );
    }

    @Test
    void shouldNotCompleteTaskWhenEvidenceHandledIsNotNo() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getEvidenceHandled()).thenReturn(YES);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                any()))
                .thenReturn(Optional.of("encodedClientContext"));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContextEvidenceHandled(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList("encodedClientContext"));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
                .test(callbackRequest, ENCODED_CLIENT_CONTEXT)).isFalse();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
    }

    @Test
    void shouldCompleteTaskWhenEvidenceHandledIsNo() throws JsonProcessingException {
        // Mock setup
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getEvidenceHandled()).thenReturn(NO);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        // Mock task completion
        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                any()))
                .thenReturn(Optional.of("encodedClientContext"));

        // Execute the method
        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContextEvidenceHandled(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        // Assertions
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList("encodedClientContext"));

        // Verify predicate logic
        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        // Ensure the predicate evaluates to true
        assertThat(predicateArgumentCaptor.getValue()
                .test(callbackRequest, ENCODED_CLIENT_CONTEXT)).isTrue();

        // Verify logging
        verify(objectMapper).writeValueAsString(callbackRequest);
    }

    @Test
    void shouldBypassWaCompletionFlagWhenEvidenceHandled() {
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(false);

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContextEvidenceHandled(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenBindingResultHasErrorsForEvidenceHandled() {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        assertThatThrownBy(() ->
                waTaskContoller.updateClientContextEvidenceHandled(
                        callbackRequest,
                        null,
                        bindingResult,
                        httpServletRequest
                )
        ).isInstanceOf(BadRequestException.class);

        verifyNoInteractions(taskUtils);
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

    @Test
    void shouldThrowBadRequestExceptionWhenBindingResultHasErrorsOnCloseReadyToIssueHandOffs() {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        assertThatThrownBy(() ->
                waTaskContoller.closeReadyToIssueHandOffs(
                        callbackRequest,
                        bindingResult,
                        httpServletRequest
                )
        ).isInstanceOf(BadRequestException.class);

        verifyNoInteractions(waTaskService);
    }
}