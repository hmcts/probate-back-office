package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaTaskServiceTest {
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;

    @InjectMocks
    private WaTaskService waTaskService;

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