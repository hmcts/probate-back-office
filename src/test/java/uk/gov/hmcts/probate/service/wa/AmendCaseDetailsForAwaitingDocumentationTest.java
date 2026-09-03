package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Spy
    private WaTaskService waTaskService;

    @InjectMocks
    private AmendCaseDetailsForAwaitingDocumentation processor;

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