package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleEvidenceTest {

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;
    @Mock
    private WaTaskService waTaskService;
    @Mock
    private ResponseCaseData responseCaseData;

    @InjectMocks
    private HandleEvidence processor;

    @BeforeEach
    void setUp() {
        when(caseDetails.getId())
                .thenReturn(1234L);

        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        when(caseDetails.getData())
                .thenReturn(caseData);
    }

    @Test
    void shouldProcessTaskCreation() {
        processor.process(null,
                callbackRequest,
                responseCaseData,
                null);
        verify(waTaskService).createTaskIfRequired(processor.getEventId(),
                callbackRequest,
                responseCaseData);
    }
}
