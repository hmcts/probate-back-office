package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.Constants.CASE_PRINTED_NAME;

class CaseDataTransformerTest {

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private EvidenceHandledTransformer evidenceHandledTransformer;
    @Mock
    private AttachDocumentsTransformer attachDocumentsTransformer;


    @InjectMocks
    private CaseDataTransformer caseDataTransformer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

    }

    @Test
    void shouldTransformEvidenceHandledForCasePrinted() {
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);

        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequestMock);
        verify(evidenceHandledTransformer).updateEvidenceHandled(caseDataMock);
    }

    @Test
    void shouldNotTransformEvidenceHandledForNotCasePrinted() {
        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequestMock);
        verify(evidenceHandledTransformer, times(0)).updateEvidenceHandled(caseDataMock);
    }

    @Test
    void shouldTransformEvidenceHandledForManualCreateByCWCasePrinted() {
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);

        caseDataTransformer.transformCaseDataForEvidenceHandledForManualCreateByCW(callbackRequestMock);
        verify(evidenceHandledTransformer).updateEvidenceHandledToNo(caseDataMock);
    }

    @Test
    void shouldNotTransformEvidenceHandledForManualCreateByCWNotCasePrinted() {
        caseDataTransformer.transformCaseDataForEvidenceHandledForManualCreateByCW(callbackRequestMock);
        verify(evidenceHandledTransformer, times(0)).updateEvidenceHandledToNo(caseDataMock);
    }

    @Test
    void shouldTransformEvidenceHandledForCreateBulkscanCasePrinted() {
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);

        caseDataTransformer.transformCaseDataForEvidenceHandledForCreateBulkscan(callbackRequestMock);
        verify(evidenceHandledTransformer).updateEvidenceHandledToNo(caseDataMock);
    }

    @Test
    void shouldNotTransformEvidenceHandledForCreateBulkscanNotCasePrinted() {
        caseDataTransformer.transformCaseDataForEvidenceHandledForCreateBulkscan(callbackRequestMock);
        verify(evidenceHandledTransformer, times(0)).updateEvidenceHandledToNo(caseDataMock);
    }

    @Test
    void shouldTransformForAttachDocuments() {
        when(caseDetailsMock.getState()).thenReturn(CASE_PRINTED_NAME);

        caseDataTransformer.transformCaseDataForAttachDocuments(callbackRequestMock);
        verify(attachDocumentsTransformer).updateAttachDocuments(caseDataMock);
    }

    @Test
    void shouldNotTransformAttachDocs() {
        caseDataTransformer.transformCaseDataForAttachDocuments(callbackRequestMock);
        verify(attachDocumentsTransformer, times(0)).updateAttachDocuments(caseDataMock);
    }

    @Test
    void shouldTransformDocsReceivedNotificationSent() {
        caseDataTransformer.transformCaseDataForDocsReceivedNotificationSent(callbackRequestMock);
        verify(attachDocumentsTransformer).updateDocsReceivedNotificationSent(caseDataMock);
    }
}
