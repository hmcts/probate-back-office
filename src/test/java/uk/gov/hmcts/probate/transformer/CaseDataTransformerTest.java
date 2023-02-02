package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorApplicationCompletionTransformer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.cases.CaseState.Constants.CASE_PRINTED_NAME;

class CaseDataTransformerTest {

    @Mock
    private EvidenceHandledTransformer evidenceHandledTransformer;
    @Mock
    private AttachDocumentsTransformer attachDocumentsTransformer;
    @Mock
    private ResetCaseDataTransformer resetCaseDataTransformer;
    @Mock
    private SolicitorApplicationCompletionTransformer solicitorApplicationCompletionTransformer;
    @Mock
    private LegalStatementExecutorTransformer legalStatementExecutorTransformer;

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private CaseDataTransformer caseDataTransformer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

    }

    @Test
    void shouldTransformForSolicitorCompletion() {
        caseDataTransformer.transformForSolicitorApplicationCompletion(callbackRequestMock);

        verify(resetCaseDataTransformer).resetExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer)
                .setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).mapSolicitorExecutorFieldsOnCompletion(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).clearSolicitorExecutorLists(caseDataMock);
    }

    @Test
    void shouldTransformForSolicitorCompletionWithServiceRequestReference() {
        String serviceRequestReference = "serviceRequestReference";
        caseDataTransformer.transformForSolicitorApplicationCompletion(callbackRequestMock,serviceRequestReference);

        verify(resetCaseDataTransformer).resetExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer)
                .setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).mapSolicitorExecutorFieldsOnCompletion(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).clearSolicitorExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).setFieldsOnServiceRequest(caseDetailsMock,
                serviceRequestReference);
    }

    @Test
    void shouldTransformForValidateProbate() {
        caseDataTransformer.transformCaseDataForValidateProbate(callbackRequestMock);

        verify(resetCaseDataTransformer).resetExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer)
                .setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).mapSolicitorExecutorFieldsOnAppDetailsComplete(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).eraseCodicilAddedDateIfWillHasNoCodicils(caseDataMock);
    }

    @Test
    void shouldTransformForValidateAdmon() {
        caseDataTransformer.transformCaseDataForValidateAdmon(callbackRequestMock);

        verify(solicitorApplicationCompletionTransformer).formatFields(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).eraseCodicilAddedDateIfWillHasNoCodicils(caseDataMock);
    }

    @Test
    void shouldTransformCaseDataForSolicitorExecutorNames() {
        caseDataTransformer.transformCaseDataForSolicitorExecutorNames(callbackRequestMock);

        verify(resetCaseDataTransformer).resetExecutorLists(caseDataMock);
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
}
