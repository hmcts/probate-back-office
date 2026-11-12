package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_CASE_WORKER_ESCALATION;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;

@ExtendWith(MockitoExtension.class)
class HandleEvidenceTest {

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;
    @Mock
    private WaTaskService waTaskService;
    @InjectMocks
    private HandleEvidence processor;
    private ResponseCaseData responseCaseData;

    @BeforeEach
    void setUp() {
        responseCaseData = ResponseCaseData.builder()
                .build();

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(caseData);
    }


    @ParameterizedTest(name = "State: {0}, Case type: {1}, create task: {2}")
    @MethodSource("taskScenarios")
    void shouldProcessTaskCreation(
            String state,
            String caseType,
            boolean shouldCreateTask) {

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getState()).thenReturn(state);
        when(caseDetails.getId()).thenReturn(123456789L);
        when(caseData.getCaseType()).thenReturn(caseType);

        TaskTypes taskType;
        switch (state) {
            case STATE_CASE_PRINTED:
                taskType = TaskTypes.getAwaitingDocIceTask(caseType).orElseThrow();
                break;
            case STATE_BO_READY_TO_ISSUE:
                taskType = TaskTypes.getReadyToIssueIceTask(caseType).orElseThrow();
                break;
            case STATE_BO_CASE_WORKER_ESCALATION:
                taskType = TaskTypes.EXAMINE_SME_REFERRAL;
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + state);
        }

        doAnswer(invocation -> {
            ResponseCaseData response = invocation.getArgument(1);
            response.setCreateTask(shouldCreateTask ? YES : NO);
            return null;
        }).when(waTaskService)
                .setCreateTask(
                        same(callbackRequest),
                        same(responseCaseData),
                        eq(taskType));


        processor.process(
                "auth-token",
                callbackRequest,
                responseCaseData,
                null
        );


        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(shouldCreateTask ? YES : NO);

        verify(waTaskService).setCreateTask(
                callbackRequest,
                responseCaseData,
                taskType
        );

    }

    private static Stream<Arguments> taskScenarios() {
        return Stream.of(
                Arguments.of(STATE_CASE_PRINTED, "gop", true),
                Arguments.of(STATE_CASE_PRINTED, "gop", false),
                Arguments.of(STATE_BO_READY_TO_ISSUE, "gop", true),
                Arguments.of(STATE_BO_READY_TO_ISSUE, "gop", false),
                Arguments.of(STATE_CASE_PRINTED, "intestacy", true),
                Arguments.of(STATE_BO_READY_TO_ISSUE, "intestacy", false),
                Arguments.of(STATE_BO_CASE_WORKER_ESCALATION, "gop", false)
        );
    }

    @Test
    void shouldSetCreateTaskToYesForUnsupportedState() {

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getState()).thenReturn("OtherState");
        when(caseDetails.getId()).thenReturn(123456789L);
        when(caseData.getCaseType()).thenReturn("gop");

        processor.process(
                "auth-token",
                callbackRequest,
                responseCaseData,
                null
        );

        assertThat(responseCaseData.getCreateTask()).isEqualTo(YES);

        verifyNoInteractions(waTaskService);
    }
}
