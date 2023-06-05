package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.probate.service.SaveNocService;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NoticeOfChangeUnitTest {

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private PrepareNocService prepareNocServiceMock;
    @Mock
    private SaveNocService saveNocServiceMock;

    private NoticeOfChangeController underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new NoticeOfChangeController(prepareNocServiceMock, saveNocServiceMock);
    }

    @Test
    void shouldApplyDecision() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        underTest.applyDecision("auth", callbackRequestMock);
        verify(prepareNocServiceMock, times(1))
                .applyDecision(Mockito.any(CallbackRequest.class), Mockito.anyString());
    }

    @Test
    void shouldAddRepresentative() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(bindingResultMock.hasErrors()).thenReturn(false);
        underTest.submittedNoCRequest(callbackRequestMock);
        verify(saveNocServiceMock, times(1))
                .addRepresentatives(Mockito.any(CallbackRequest.class));
    }
}
