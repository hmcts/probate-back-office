package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmendCaseDetailsForReadyToIssueTest {
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseDetails caseDetailsBefore;

    @InjectMocks
    private AmendCaseDetailsForReadyToIssue processor;

    @Test
    void shouldReturnCorrectEventId() {
        assertThat(processor.getEventId())
                .isEqualTo("boAmendCaseDetailsForReadyToIssue");
    }

    @Test
    void shouldSetCreateTaskToNoWhenCaseTypesAreSame() {
        setUpCallbackRequest(
                "GrantOfRepresentation",
                "GrantOfRepresentation"
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.NO);
    }

    @Test
    void shouldSetCreateTaskToYesWhenCaseTypesAreDifferent() {
        setUpCallbackRequest(
                "CaveatGrantOfRepresentation",
                "Caveat"
        );

        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();
        processor.process(callbackRequest, responseCaseData);

        assertThat(responseCaseData.getCreateTask())
                .isEqualTo(Constants.YES);
    }

    private void setUpCallbackRequest(
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