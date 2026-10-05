package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WaTaskServiceTest {
    public static final String EVENT_TO_MONITOR = "boAmendCaseDetailsForAwaitingDocumentation";
    @Mock
    private WaApi waApi;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

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
    void shouldCreateAndSubmitTaskForCaseWorkerSuccessfully() {
        // Mock CallbackRequest and CaseDetails
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails caseDetails =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseData caseData =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.class);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getId()).thenReturn(0L); // Ensure this matches the expected value

        // Mock SecurityDTO
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("auth")
                .userId("userId")
                .serviceAuthorisation("serviceAuth")
                .build();

        // Mock StartEventResponse
        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId("eventId")
                .token("eventToken")
                .caseDetails(mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class))
                .build();

        // Update stubbing to match actual arguments
        when(coreCaseDataApi.startEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null), // Matches the null value in the actual call
                eq("0"), // Matches the case ID as a string
                eq("autoSelectForQACreateTask")
        )).thenReturn(startEventResponse);

        // Act
        waTaskService.createAndSubmitTaskForCaseWorker(
                callbackRequest,
                securityDTO,
                EventId.AUTO_SELECT_FOR_QA_CREATE_TASK,
                "summary",
                "description"
        );

        // Verify interactions
        verify(coreCaseDataApi).startEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null),
                eq("0"),
                eq("autoSelectForQACreateTask")
        );

        verify(coreCaseDataApi).submitEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null), // Matches the null value in the actual call
                eq("0"), // Matches the case ID as a string
                eq(false),
                any(CaseDataContent.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenStartEventFails() {
        // Mock CallbackRequest and CaseDetails
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails caseDetails =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseData caseData =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.class);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData); // Ensure getData() does not return null
        when(caseDetails.getId()).thenReturn(0L); // Ensure this matches the expected value

        // Mock SecurityDTO
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("auth")
                .userId("userId")
                .serviceAuthorisation("serviceAuth")
                .build();

        // Simulate exception when startEventForCaseWorker is called
        when(coreCaseDataApi.startEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null),
                eq("0"),
                eq("autoSelectForQACreateTask")
        )).thenThrow(new RuntimeException("Start event failed"));

        // Assert that the exception is thrown with the correct message
        assertThatThrownBy(() -> waTaskService.createAndSubmitTaskForCaseWorker(
                callbackRequest,
                securityDTO,
                EventId.AUTO_SELECT_FOR_QA_CREATE_TASK,
                "summary",
                "description"
        )).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Start event failed");

        // Verify that submitEventForCaseWorker is never called
        verify(coreCaseDataApi, never()).submitEventForCaseWorker(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyBoolean(),
                any(CaseDataContent.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenSubmitEventFails() {
        // Mock CallbackRequest and CaseDetails
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails caseDetails =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails.class);
        uk.gov.hmcts.probate.model.ccd.raw.request.CaseData caseData =
                mock(uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.class);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData); // Ensure getData() does not return null
        when(caseDetails.getId()).thenReturn(0L); // Ensure this matches the expected value

        // Mock SecurityDTO
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("auth")
                .userId("userId")
                .serviceAuthorisation("serviceAuth")
                .build();

        // Mock StartEventResponse
        StartEventResponse startEventResponse = StartEventResponse.builder()
                .eventId("eventId")
                .token("eventToken")
                .caseDetails(mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class))
                .build();

        when(coreCaseDataApi.startEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null),
                eq("0"),
                eq("autoSelectForQACreateTask")
        )).thenReturn(startEventResponse);

        // Simulate exception when submitEventForCaseWorker is called
        when(coreCaseDataApi.submitEventForCaseWorker(
                eq("auth"),
                eq("serviceAuth"),
                eq("userId"),
                eq("PROBATE"),
                eq(null),
                eq("0"),
                eq(false),
                any(CaseDataContent.class)
        )).thenThrow(new RuntimeException("Submit event failed"));

        // Assert that the exception is thrown with the correct message
        assertThatThrownBy(() -> waTaskService.createAndSubmitTaskForCaseWorker(
                callbackRequest,
                securityDTO,
                EventId.AUTO_SELECT_FOR_QA_CREATE_TASK,
                "summary",
                "description"
        )).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Submit event failed");
    }
}