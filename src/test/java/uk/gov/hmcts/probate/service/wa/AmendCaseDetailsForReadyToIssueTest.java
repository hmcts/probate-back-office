package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmendCaseDetailsForReadyToIssueTest {
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;
    @Spy
    private WaTaskService waTaskService;

    @InjectMocks
    private AmendCaseDetailsForReadyToIssue processor;

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId())
                .isEqualTo("boAmendCaseDetailsForReadyToIssue");
    }

    @Test
    void shouldSetCreateTaskToNoWhenCaseTypesAreSame() {
        setUpCaseTypeCallbackRequest(
                CaseType.GRANT_OF_REPRESENTATION.name(),
                CaseType.GRANT_OF_REPRESENTATION.name()
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.NO);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToYesWhenCaseTypesAreDifferent() {
        setUpCaseTypeCallbackRequest(
                CaseType.GRANT_OF_REPRESENTATION.name(),
                CaseType.CAVEAT.name()
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.YES);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToNoWhenHandOffReasonsAreSame() {
        setUpHandOffReasonsCallbackRequest(
                HandoffReasonId.AD_COLLIGENDA_BONA,
                HandoffReasonId.AD_COLLIGENDA_BONA
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandOffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .isEmpty();

        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToYesWhenHandOffReasonsAreDifferent() {
        setUpHandOffReasonsCallbackRequest(
                HandoffReasonId.FIAT_WILL,
                HandoffReasonId.CODICIL_MIS
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandOffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .containsExactlyInAnyOrder(
                        HandoffReasonId.FIAT_WILL);

        verify(waTaskService)
                .getCaseTypePredicate();
    }

    private void setUpCaseTypeCallbackRequest(
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

    private void setUpHandOffReasonsCallbackRequest(
            HandoffReasonId handOffReasonId,
            HandoffReasonId handOffReasonBeforeId) {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .boHandoffReasonList(List.of(new CollectionMember<>(null,
                                HandoffReason.builder().caseHandoffReason(handOffReasonId).build())))
                        .build());
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .boHandoffReasonList(List.of(new CollectionMember<>(null,
                                HandoffReason.builder().caseHandoffReason(handOffReasonBeforeId).build())))
                        .build());
    }
}