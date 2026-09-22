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
import uk.gov.hmcts.probate.model.wa.TaskTypes;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.utils.TaskUtils;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_ADMON_WILL;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.model.wa.TaskTypes.EXAMINE_DIGITAL_CASE_PROBATE;

@ExtendWith(MockitoExtension.class)
class AmendCaseDetailsForReadyToIssueTest {

    public static final String BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION
            = "boAmendCaseDetailsForAwaitingDocumentation";
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;
    @Mock
    private WaApi waApi;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private TaskUtils taskUtils;

    private WaTaskService waTaskService;

    private AmendCaseDetailsForReadyToIssue processor;

    private static final String AUTH_TOKEN = "authToken";

    private final List<TaskTypes> taskToCLose = List.of(EXAMINE_DIGITAL_CASE_PROBATE,
            EXAMINE_DIGITAL_CASE_INTESTACY,
            EXAMINE_DIGITAL_CASE_ADMON_WILL,
            EXAMINE_DIGITAL_CASE_ADCOLLIGENDA_BONA);

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(waApi, securityUtils, taskUtils));
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

        String caseId = callbackRequest.getCaseDetails().getId().toString();

        doReturn(false)
                .when(waTaskService).isTaskPresent(AUTH_TOKEN, caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION, taskToCLose);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.NO);
        verify(waTaskService)
                .getCaseTypePredicate();
        verify(waTaskService)
                .isTaskPresent(AUTH_TOKEN, callbackRequest.getCaseDetails().getId().toString(),
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                        taskToCLose);
    }

    @Test
    void shouldSetCreateTaskToNoWhenCaseTypesAreSameWithTaskToClosePresent() {
        setUpCaseTypeCallbackRequest(
                CaseType.GRANT_OF_REPRESENTATION.name(),
                CaseType.CAVEAT.name()
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.YES);
        verify(waTaskService)
                .getCaseTypePredicate();
        verify(waTaskService, never())
                .isTaskPresent(AUTH_TOKEN, callbackRequest.getCaseDetails().getId().toString(),
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION,
                        taskToCLose);
    }

    @Test
    void shouldSetCreateTaskToYesWhenCaseTypesAreDifferent() {
        setUpCaseTypeCallbackRequest(
                "CaveatGrantOfRepresentation",
                "Caveat"
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.YES);
        verify(waTaskService).getCaseTypePredicate();
    }

    @Test
    void shouldSetCreateTaskToNoWhenHandOffReasonsAreSame() {
        setUpHandOffReasonsCallbackRequest(
                HandoffReasonId.AD_COLLIGENDA_BONA,
                HandoffReasonId.AD_COLLIGENDA_BONA
        );
        String caseId = callbackRequest.getCaseDetails().getId().toString();

        doReturn(false)
                .when(waTaskService).isTaskPresent(AUTH_TOKEN, caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION, taskToCLose);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .isEmpty();

        verify(waTaskService)
                .getCaseTypePredicate();

        verify(waTaskService).isTaskPresent(AUTH_TOKEN, callbackRequest.getCaseDetails().getId().toString(),
                BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION, taskToCLose);
    }

    @Test
    void shouldSetCreateTaskToYesWhenHandOffReasonsAreDifferent() {
        setUpHandOffReasonsCallbackRequest(
                HandoffReasonId.FIAT_WILL,
                HandoffReasonId.CODICIL_MIS
        );
        String caseId = callbackRequest.getCaseDetails().getId().toString();

        doReturn(false)
                .when(waTaskService).isTaskPresent(AUTH_TOKEN, caseId,
                        BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION, taskToCLose);

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(AUTH_TOKEN, callbackRequest, responseCaseData);

        assertThat(responseCaseData.getWaHandoffReasonList())
                .extracting(CollectionMember::getValue)
                .extracting(HandoffReason::getCaseHandoffReason)
                .containsExactlyInAnyOrder(
                        HandoffReasonId.FIAT_WILL);

        verify(waTaskService)
                .getCaseTypePredicate();
        verify(waTaskService).isTaskPresent(AUTH_TOKEN, callbackRequest.getCaseDetails().getId().toString(),
                BO_AMEND_CASE_DETAILS_FOR_AWAITING_DOCUMENTATION, taskToCLose);
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