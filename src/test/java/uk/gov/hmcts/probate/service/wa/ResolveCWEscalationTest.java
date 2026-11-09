package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveCWEscalationTest {
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private ResponseCaseData responseCaseData;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private WaTaskService waTaskService;
    @InjectMocks
    private ResolveCWEscalation processor;

    @Test
    void shouldInvokeWaTaskService() {
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getId()).thenReturn(123456789L);

        String clientContext = "client_context";
        processor.process(null,
                callbackRequest,
                responseCaseData,
                clientContext);
        verify(waTaskService)
                .removeTaskFromWaTaskPresent(callbackRequest,
                        responseCaseData,
                        clientContext);
    }
}