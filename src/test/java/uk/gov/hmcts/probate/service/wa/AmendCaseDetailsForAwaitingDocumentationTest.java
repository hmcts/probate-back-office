package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.utils.TaskUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmendCaseDetailsForAwaitingDocumentationTest {
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

    private AmendCaseDetailsForAwaitingDocumentation processor;

    private static final String AUTH_TOKEN = "authToken";

    @BeforeEach
    void setUp() {
        waTaskService = spy(new WaTaskService(waApi, securityUtils, taskUtils));
        processor = new AmendCaseDetailsForAwaitingDocumentation(waTaskService);
    }

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId())
                .isEqualTo("boAmendCaseDetailsForAwaitingDocumentation");
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
                .isEqualTo(Constants.YES);
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

}