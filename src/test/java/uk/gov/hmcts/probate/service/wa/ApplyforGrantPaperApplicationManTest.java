package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.wa.TaskTypes;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.DocumentCaseType.GOP;
import static uk.gov.hmcts.probate.model.DocumentCaseType.INTESTACY;

@ExtendWith(MockitoExtension.class)
class ApplyforGrantPaperApplicationManTest {
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;

    @InjectMocks
    private ApplyforGrantPaperApplicationMan processor;


    @ParameterizedTest(name = "Case type: {0}, Expected task: {1}")
    @MethodSource("taskScenarios")
    void shouldAddAwaitingDocumentationTask(
            String caseType,
            String expectedTaskName) {

        ResponseCaseData responseCaseData = ResponseCaseData.builder()
                .waTasksPresent(new ArrayList<>())
                .build();

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);


        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType(caseType)
                        .caseHandedOffToLegacySite(NO)
                        .build());

        processor.process(
                "auth-token",
                callbackRequest,
                responseCaseData,
                "client-context"
        );

        if (expectedTaskName != null) {

            assertThat(responseCaseData.getWaTasksPresent())
                    .hasSize(1);

            CollectionMember<String> task =
                    responseCaseData.getWaTasksPresent().get(0);

            assertThat(task.getValue())
                    .isEqualTo(expectedTaskName);

            assertThatCode(() -> UUID.fromString(task.getId()))
                    .doesNotThrowAnyException();

        } else {
            assertThat(responseCaseData.getWaTasksPresent())
                    .isEmpty();
        }
    }

    private static Stream<Arguments> taskScenarios() {
        return Stream.of(
                Arguments.of(
                        GOP.getCaseType(),
                        TaskTypes.getAwaitingDocIceTask(GOP.getCaseType())
                                .map(TaskTypes::getValue)
                                .orElse(null)
                ),
                Arguments.of(
                        INTESTACY.getCaseType(),
                        TaskTypes.getAwaitingDocIceTask(INTESTACY.getCaseType())
                                .map(TaskTypes::getValue)
                                .orElse(null)
                ),
                Arguments.of("unknown", null)
        );
    }
}