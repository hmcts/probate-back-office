package uk.gov.hmcts.probate.transformer;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.transformer.reset.ResetCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.LegalStatementExecutorTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.SolicitorApplicationCompletionTransformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
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
    @Mock
    private ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    @InjectMocks
    private CaseDataTransformer caseDataTransformer;

    @BeforeEach
    public void setUp() {
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
        BigDecimal totalAmount = BigDecimal.valueOf(100000);
        caseDataTransformer.transformForSolicitorApplicationCompletion(callbackRequestMock,totalAmount);

        verify(resetCaseDataTransformer).resetExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer)
                .setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).mapSolicitorExecutorFieldsOnCompletion(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).clearSolicitorExecutorLists(caseDataMock);
        verify(solicitorApplicationCompletionTransformer).setFieldsOnServiceRequest(caseDetailsMock,
                totalAmount);
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

    @Test
    void shouldTransformDocsReceivedNotificationSent() {
        caseDataTransformer.transformCaseDataForDocsReceivedNotificationSent(callbackRequestMock);
        verify(attachDocumentsTransformer).updateDocsReceivedNotificationSent(caseDataMock);
    }

    @Test
    void shouldTransformFormSelection() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormId("IHT205")
                .hmrcLetterId("No").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionFormEstate400() {
        caseDataMock = CaseData.builder().ihtFormEstate("IHT400")
                .ihtFormId("IHT400")
                .hmrcLetterId("No").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionFormEstate400421AndFormId400() {
        caseDataMock = CaseData.builder().ihtFormEstate("IHT400421")
                .ihtFormId("IHT400")
                .hmrcLetterId("No").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionForDiedAfter() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400421")
                .ihtFormId("IHT400")
                .ihtFormEstateValuesCompleted(YES)
                .hmrcLetterId("No").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionForDiedAfterFormEstate400() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormId("IHT400")
                .ihtFormEstateValuesCompleted(YES)
                .hmrcLetterId("No").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionForDiedAfterEE() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormId("IHT400")
                .hmrcLetterId("No")
                .ihtFormEstateValuesCompleted(NO).build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
    }

    @Test
    void shouldTransformFormSelectionForDiedAfterIHT400() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormEstateValuesCompleted("Yes")
                .ihtEstateGrossValue(new BigDecimal(new BigInteger("100"), 0))
                .ihtEstateNetValue(new BigDecimal(new BigInteger("100"), 0))
                .ihtEstateNetQualifyingValue(new BigDecimal(new BigInteger("100"), 0)).build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
        assertThat(caseDataMock.getIhtFormEstate(), is("IHT400"));
        assertThat(caseDataMock.getIhtEstateGrossValue(), CoreMatchers.is(nullValue()));
        assertThat(caseDataMock.getIhtEstateNetValue(), CoreMatchers.is(nullValue()));
        assertThat(caseDataMock.getIhtEstateNetQualifyingValue(), CoreMatchers.is(nullValue()));
    }

    @Test
    void shouldTransformFormSelectionForDiedAfterIhtNA() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("NA")
                .ihtFormEstateValuesCompleted("Yes")
                .ihtEstateGrossValue(new BigDecimal(new BigInteger("100"), 0))
                .ihtEstateNetValue(new BigDecimal(new BigInteger("100"), 0))
                .ihtEstateNetQualifyingValue(new BigDecimal(new BigInteger("100"), 0)).build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformFormCaseData(callbackRequestMock);
        assertThat(caseDataMock.getIhtFormEstate(), is("NA"));
        assertThat(caseDataMock.getIhtEstateGrossValue(), is(new BigDecimal(new BigInteger("100"), 0)));
        assertThat(caseDataMock.getIhtEstateNetValue(), is(new BigDecimal(new BigInteger("100"), 0)));
        assertThat(caseDataMock.getIhtEstateNetQualifyingValue(),
                is(new BigDecimal(new BigInteger("100"), 0)));
    }

    @Test
    void shouldTransformCaseDataForPaperForm() {
        caseDataMock = CaseData.builder().build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        caseDataTransformer.transformCaseDataForPaperForm(callbackRequestMock);
        assertThat(caseDataMock.getChannelChoice(), is("PaperForm"));
    }

    @Test
    void shouldTransformIhtFormIdNullForDiedAfter() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormId("IHT205").build();

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(true);
        caseDataTransformer.transformIhtFormCaseDataByDeceasedDOD(callbackRequestMock);
        assertThat(caseDataMock.getIhtFormId(), CoreMatchers.is(nullValue()));
    }

    @Test
    void shouldTransformIhtFormEstateNullForDiedBefore() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL)
                .ihtFormEstate("IHT400")
                .ihtFormId("IHT205").build();


        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate((LocalDate) any())).thenReturn(false);
        caseDataTransformer.transformIhtFormCaseDataByDeceasedDOD(callbackRequestMock);
        assertThat(caseDataMock.getIhtFormEstate(), CoreMatchers.is(nullValue()));
    }

    @Test
    void shouldSetApplicationSubmittedDate() {
        caseDataMock = CaseData.builder().applicationType(ApplicationType.PERSONAL).build();
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        caseDataTransformer.setApplicationSubmittedDateForPA(caseDetailsMock);
        assertEquals(LocalDate.now().toString(),
                caseDetailsMock.getData().getApplicationSubmittedDate());
    }

}
