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
import static uk.gov.hmcts.probate.model.StateConstants.STATE_BO_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;
import static uk.gov.hmcts.probate.service.wa.DeathRecordVerified.DEATH_RECORD_VERIFIED;

@ExtendWith(MockitoExtension.class)
class DeathRecordVerifiedTest {

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;
    @Mock
    private WaTaskService waTaskService;
    @InjectMocks
    private DeathRecordVerified processor;
    private ResponseCaseData responseCaseData;

    @BeforeEach
    void setUp() {
        responseCaseData = ResponseCaseData.builder().build();
    }

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId()).isEqualTo(DEATH_RECORD_VERIFIED);
    }

    @ParameterizedTest(name = "caseType: {0}, shouldCreateTask: {1}")
    @MethodSource("casePrintedScenarios")
    void shouldCreateTaskWhenStateIsCasePrinted(String caseType, boolean shouldCreateTask) {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getState()).thenReturn(STATE_CASE_PRINTED);
        when(caseDetails.getId()).thenReturn(123456789L);
        when(caseData.getCaseType()).thenReturn(caseType);

        TaskTypes taskType = TaskTypes.getAwaitingDocIceTask(caseType).orElseThrow();

        doAnswer(invocation -> {
            ResponseCaseData response = invocation.getArgument(1);
            response.setCreateTask(shouldCreateTask ? YES : NO);
            return null;
        }).when(waTaskService).setCreateTask(same(callbackRequest), same(responseCaseData), eq(taskType));

        processor.process("auth-token", callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(shouldCreateTask ? YES : NO);
        verify(waTaskService).setCreateTask(callbackRequest, responseCaseData, taskType);
    }

    @Test
    void shouldNotCreateTaskWhenStateIsCasePrintedAndCaseTypeUnknown() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getState()).thenReturn(STATE_CASE_PRINTED);
        when(caseDetails.getId()).thenReturn(123456789L);
        when(caseData.getCaseType()).thenReturn("unknownCaseType");

        processor.process("auth-token", callbackRequest, responseCaseData, null);

        verifyNoInteractions(waTaskService);
        assertThat(responseCaseData.getCreateTask()).isNull();
    }

    @Test
    void shouldSetCreateTaskToYesWhenStateIsNotCasePrinted() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseDetails.getState()).thenReturn(STATE_BO_READY_TO_ISSUE);
        when(caseDetails.getId()).thenReturn(123456789L);
        when(caseData.getCaseType()).thenReturn("intestacy");

        processor.process("auth-token", callbackRequest, responseCaseData, null);

        assertThat(responseCaseData.getCreateTask()).isEqualTo(YES);
        verifyNoInteractions(waTaskService);
    }

    private static Stream<Arguments> casePrintedScenarios() {
        return Stream.of(
                Arguments.of("intestacy", true),
                Arguments.of("intestacy", false),
                Arguments.of("gop", true),
                Arguments.of("admonWill", true)
        );
    }
}
