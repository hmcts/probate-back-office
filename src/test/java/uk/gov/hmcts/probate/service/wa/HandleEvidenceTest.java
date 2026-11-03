package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.YES;
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
    private SecurityUtils securityUtils;
    @Mock
    private TaskUtils taskUtils;
    @Mock
    private CcdClientApi ccdClientApi;

    private WaTaskService waTaskService;
    private HandleEvidence processor;
    private ResponseCaseData responseCaseData;

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(securityUtils, taskUtils, ccdClientApi));
        processor = new HandleEvidence(waTaskService);
        responseCaseData = ResponseCaseData.builder()
                .waTasksPresent(new ArrayList<>())
                .build();

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(caseData);
    }

    @ParameterizedTest(
            name = "[{index}] State: {0}, Case Type: {1}"
    )
    @MethodSource("taskScenarios")
    void shouldSetCreateTaskBasedOnCaseState(
            String state,
            String caseType,
            Optional<TaskTypes> expectedTaskType) {

        when(caseDetails.getState()).thenReturn(state);
        when(caseData.getCaseType()).thenReturn(caseType);

        processor.process(
                "auth-token",
                callbackRequest,
                responseCaseData,
                null
        );

        assertThat(responseCaseData.getCreateTask()).isEqualTo(YES);

        verify(waTaskService).setCreateTask(
                same(callbackRequest),
                same(responseCaseData),
                eq(expectedTaskType)
        );
    }

    private static Stream<Arguments> taskScenarios() {
        return Stream.of(
                Arguments.of(
                        STATE_CASE_PRINTED,
                        "gop",
                        TaskTypes.getAwaitingDocIceTask("gop")
                ),
                Arguments.of(
                        STATE_CASE_PRINTED,
                        "intestacy",
                        TaskTypes.getAwaitingDocIceTask("intestacy")
                ),
                Arguments.of(
                        STATE_BO_READY_TO_ISSUE,
                        "gop",
                        TaskTypes.getReadyToIssueIceTask("gop")
                ),
                Arguments.of(
                        STATE_BO_READY_TO_ISSUE,
                        "intestacy",
                        TaskTypes.getReadyToIssueIceTask("intestacy")
                )
        );
    }
}
