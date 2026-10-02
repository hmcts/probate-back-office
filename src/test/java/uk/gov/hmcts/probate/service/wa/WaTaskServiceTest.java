package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WaTaskServiceTest {
    public static final String EVENT_TO_MONITOR = "boAmendCaseDetailsForAwaitingDocumentation";
    @Mock
    private WaApi waApi;

    @Mock
    private SecurityUtils securityUtils;

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
}