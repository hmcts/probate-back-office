package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.wa.SearchEventAndCase;
import uk.gov.hmcts.probate.model.wa.TaskData;
import uk.gov.hmcts.probate.model.wa.GetTasksCompletableResponse;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

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

    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private SecurityUtils securityUtils;
    private CaseDetails caseDetailsBefore;

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

        Predicate<CallbackRequest> predicate =
                waTaskService.getCaseTypePredicate();

        assertThat(predicate.test(callbackRequest))
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenCaseTypeHasNotChanged() {
        setUpCallbackRequestForCaseType(
                "GrantOfProbate",
                "GrantOfProbate"
        );

        Predicate<CallbackRequest> predicate =
                waTaskService.getCaseTypePredicate();

        assertThat(predicate.test(callbackRequest))
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenHandOffReasonsHaveChanged() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        List.of(HandoffReasonId.DOUBLE_PROBATE))
                        ).build());

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        List.of(HandoffReasonId.FOREIGN_DOMICILE))
                        ).build());

        Predicate<CallbackRequest> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest))
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenHandOffReasonsHaveNotChanged() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        List.of(HandoffReasonId.DOUBLE_PROBATE))
                        ).build());

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        List.of(HandoffReasonId.DOUBLE_PROBATE))
                        ).build());

        Predicate<CallbackRequest> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest))
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenHandOffReasonsPresent() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .build());

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .build());

        Predicate<CallbackRequest> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest))
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
    void shouldReturnCurrentHandOffsWhenPreviousNoHandOffReasons() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .build());

        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .boHandoffReasonList(
                                generateHandOffReasonCollection(
                                        List.of(HandoffReasonId.DOUBLE_PROBATE,
                                                HandoffReasonId.FOREIGN_DOMICILE))
                        ).build());

        Predicate<CallbackRequest> predicate =
                waTaskService.getHandOffPredicate();

        assertThat(predicate.test(callbackRequest))
                .isTrue();
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