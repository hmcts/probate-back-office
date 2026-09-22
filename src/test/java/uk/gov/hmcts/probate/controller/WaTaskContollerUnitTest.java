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
import uk.gov.hmcts.probate.security.SecurityUtils;
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
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;

@ExtendWith(MockitoExtension.class)
class WaTaskContollerUnitTest {
    public static final String CLIENT_CONTEXT = "encodedClientContext";
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

    private WaTaskService waTaskService;

    private WaTaskContoller waTaskContoller;

    @Captor
    private ArgumentCaptor<BiPredicate<CallbackRequest, String>> predicateArgumentCaptor;

    private final String clientContext = "clientContext";

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(waApi, securityUtils, taskUtils));
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
                .thenReturn(Optional.of(CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateCaseTypeClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, CLIENT_CONTEXT)).isFalse();

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
                .thenReturn(Optional.of(CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateCaseTypeClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, CLIENT_CONTEXT)).isTrue();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldNotCompleteTheExistingTaskAndNoNewTaskCreatedForHandOffReasons() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore()).thenReturn(caseDetailsBefore);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(HandoffReasonId.DOUBLE_PROBATE)));
        when(caseDetailsBefore.getData()).thenReturn(caseDataBefore);
        when(caseDataBefore.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(HandoffReasonId.DOUBLE_PROBATE)));

        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateHandOffClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest, CLIENT_CONTEXT)).isFalse();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);
        verify(waTaskService)
                .getHandOffPredicate();
    }

    @Test
    void shouldCompleteTheExistingTaskAndNewTaskCreatedForHandOffReasons() throws JsonProcessingException {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore()).thenReturn(caseDetailsBefore);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(HandoffReasonId.DOUBLE_PROBATE)));
        when(caseDetailsBefore.getData()).thenReturn(caseDataBefore);
        when(caseDataBefore.getBoHandoffReasonList())
                .thenReturn(generateHandOffReasonCollection(List.of(HandoffReasonId.FOREIGN_DOMICILE)));
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        when(taskUtils.setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                 any()))
                .thenReturn(Optional.of(CLIENT_CONTEXT));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateHandOffClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        assertThat(response.getHeaders())
                .containsEntry(CLIENT_CONTEXT_HEADER_PARAMETER, Collections.singletonList(CLIENT_CONTEXT));

        verify(taskUtils).setTaskCompletion(
                eq(clientContext),
                eq(callbackRequest),
                predicateArgumentCaptor.capture());

        assertThat(predicateArgumentCaptor.getValue()
               .test(callbackRequest)).isTrue();

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