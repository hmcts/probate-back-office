package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.wa.DeathRecordVerificationFailed.DEATH_RECORD_VERIFICATION_FAILED;

@ExtendWith(MockitoExtension.class)
class DeathRecordVerificationFailedTest {

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;
    @Mock
    private WaTaskService waTaskService;
    @InjectMocks
    private DeathRecordVerificationFailed processor;

    @Test
    void shouldReturnEventId() {
        assertThat(processor.getEventId()).isEqualTo("deathRecordVerificationFailed");
    }

    @Test
    void shouldDelegateToWaTaskService() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
        ResponseCaseData responseCaseData = ResponseCaseData.builder().build();

        processor.process("auth-token", callbackRequest, responseCaseData, null);

        verify(waTaskService).createTaskIfRequired(DEATH_RECORD_VERIFICATION_FAILED,
                callbackRequest, responseCaseData);
    }
}