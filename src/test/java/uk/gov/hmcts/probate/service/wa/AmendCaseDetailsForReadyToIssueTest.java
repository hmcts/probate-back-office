package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@ExtendWith(MockitoExtension.class)
class AmendCaseDetailsForReadyToIssueTest {

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private TaskUtils taskUtils;
    @Mock
    private CcdClientApi ccdClientApi;

    private WaTaskService waTaskService;

    private AmendCaseDetailsForReadyToIssue processor;

    private static final String AUTH_TOKEN = "authToken";

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(securityUtils, taskUtils, ccdClientApi));
        processor = new AmendCaseDetailsForReadyToIssue(waTaskService);
    }

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

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

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

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(YES);
        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToNoWhenHandOffReasonsAreSame() {
        setUpHandOffReasonsCallbackRequest(
                List.of(HandoffReasonId.AD_COLLIGENDA_BONA),
                List.of(HandoffReasonId.AD_COLLIGENDA_BONA)
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .isEmpty();

        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToYesWhenHandOffReasonsAreDifferent() {
        setUpHandOffReasonsCallbackRequest(
                List.of(HandoffReasonId.FIAT_WILL),
                List.of(HandoffReasonId.CODICIL_MIS)
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .containsExactlyInAnyOrder(
                        HandoffReasonId.FIAT_WILL);

        verify(waTaskService)
                .getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToYesWhenAmendDocHandOffReasonsIsPresent() {
        setUpHandOffReasonsCallbackRequest(
                List.of(HandoffReasonId.FIAT_WILL,HandoffReasonId.CODICIL_MIS),
                List.of(HandoffReasonId.CODICIL_MIS)
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .containsExactlyInAnyOrder(
                        HandoffReasonId.FIAT_WILL);

        verify(waTaskService)
                .getCaseTypePredicate();

    }

    @Test
    void shouldCreateIceTaskToWhenAllHandOffReasonsAreRemoved() {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);
        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .caseHandedOffToLegacySite(NO)
                        .build());
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .caseHandedOffToLegacySite(YES)
                        .boHandoffReasonList(List.of(new CollectionMember<>(null,
                                HandoffReason.builder().caseHandoffReason(HandoffReasonId.FIAT_WILL).build())))
                        .build());

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .isNullOrEmpty();

        verify(waTaskService)
                .getCaseTypePredicate();

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(YES);
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
            List<HandoffReasonId> handOffReasonIds,
            List<HandoffReasonId> handOffReasonBeforeIds) {
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);
        when(callbackRequest.getCaseDetailsBefore())
                .thenReturn(caseDetailsBefore);

        Function<List<HandoffReasonId>, List<CollectionMember<HandoffReason>>> handOffs = handOffReasons ->
                handOffReasons.stream()
                                .map(handoffReasonId -> new CollectionMember<>(null,
                                        HandoffReason.builder().caseHandoffReason(handoffReasonId).build()))
                                .toList();


        when(caseDetails.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .caseHandedOffToLegacySite(YES)
                        .boHandoffReasonList(handOffs.apply(handOffReasonIds))
                        .build());
        when(caseDetailsBefore.getData())
                .thenReturn(CaseData.builder()
                        .caseType(CaseType.CAVEAT.name())
                        .caseHandedOffToLegacySite(YES)
                        .boHandoffReasonList(handOffs.apply(handOffReasonBeforeIds))
                        .build());
    }
}