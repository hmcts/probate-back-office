package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateDraftServiceTest {

    private UpdateDraftService updateDraftService;

    private CallbackRequest callbackRequest;
    private CaseDetails caseDetails;
    private CaseData caseData;

    @BeforeEach
    void setUp() {
        updateDraftService = new UpdateDraftService();

        callbackRequest = mock(CallbackRequest.class);
        caseDetails = mock(CaseDetails.class);
        caseData = mock(CaseData.class);

        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
    }

    @Test
    void shouldResetExecutorsApplyingAgreedFlagsWhenDeclarationDataChangedAndInviteSent() {
        when(caseData.getEventDescription()).thenReturn("/declaration");
        when(caseData.hasDataChanged()).thenReturn(true);
        when(caseData.inviteSent()).thenReturn(true);

        updateDraftService.update(callbackRequest);

        verify(caseData).resetExecutorsApplyingAgreedFlags();
    }

    @Test
    void shouldNotResetExecutorsApplyingAgreedFlagsWhenEventDescriptionIsNull() {
        when(caseData.getEventDescription()).thenReturn(null);

        updateDraftService.update(callbackRequest);

        verify(caseData, never()).resetExecutorsApplyingAgreedFlags();
    }

    @Test
    void shouldNotResetExecutorsApplyingAgreedFlagsWhenEventDescriptionIsEmpty() {
        when(caseData.getEventDescription()).thenReturn("");

        updateDraftService.update(callbackRequest);

        verify(caseData, never()).resetExecutorsApplyingAgreedFlags();
    }

    @Test
    void shouldResetWhenEventDescriptionContainsDeclarationPath() {
        when(caseData.getEventDescription()).thenReturn("/some-prefix/declaration/some-suffix");
        when(caseData.hasDataChanged()).thenReturn(true);
        when(caseData.inviteSent()).thenReturn(true);

        updateDraftService.update(callbackRequest);

        verify(caseData).resetExecutorsApplyingAgreedFlags();
    }
}