package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.utils.TaskUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CLIENT_CONTEXT_HEADER_PARAMETER;

@ExtendWith(MockitoExtension.class)
class WaTaskContollerUnitTest {
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
    private CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    private WorkAllocationToggleService workAllocationToggleService;

    @InjectMocks
    private WaTaskContoller waTaskContoller;

    @Captor
    private ArgumentCaptor<Predicate<CallbackRequest>> predicateArgumentCaptor;

    private final String clientContext = "clientContext";

    @Test
    void shouldNotCompleteTheExistingTaskAndNoNewTaskCreated() throws JsonProcessingException {
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
                .thenReturn(Optional.of("encodedClientContext"));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContext(
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
               .test(callbackRequest)).isFalse();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);

        verify(callbackResponseTransformer)
                .transform(callbackRequest, Optional.empty());
    }

    @Test
    void shouldCompleteTheExistingTaskAndNewTaskCreated() throws JsonProcessingException {
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
                .thenReturn(Optional.of("encodedClientContext"));

        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContext(
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
               .test(callbackRequest)).isTrue();

        verify(objectMapper)
                .writeValueAsString(callbackRequest);

        verify(callbackResponseTransformer)
                .transform(callbackRequest, Optional.empty());
    }

    @Test
    void shouldByPassWaCompletionFlag() {
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(false);
        ResponseEntity<CallbackResponse> response = waTaskContoller.updateClientContext(
                callbackRequest,
                clientContext,
                bindingResult,
                httpServletRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyNoInteractions(taskUtils, callbackResponseTransformer);
    }


    @Test
    void shouldThrowBadRequestExceptionWhenBindingResultHasErrors() {
        when(caseDetails.getId()).thenReturn(12345L);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(workAllocationToggleService.isProbateWAEnabled()).thenReturn(true);

        assertThatThrownBy(() ->
                waTaskContoller.updateClientContext(
                        callbackRequest,
                        null,
                        bindingResult,
                        httpServletRequest
                )
        ).isInstanceOf(BadRequestException.class);

        verifyNoInteractions(taskUtils);
        verifyNoInteractions(callbackResponseTransformer);
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
                waTaskContoller.updateClientContext(
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
}