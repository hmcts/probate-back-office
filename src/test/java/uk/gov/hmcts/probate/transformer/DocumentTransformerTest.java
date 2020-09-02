package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.ASSEMBLED_LETTER;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVER;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_COVERSHEET;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOT_INFORMATION_REQUEST;
import static uk.gov.hmcts.probate.model.DocumentType.STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT;

public class DocumentTransformerTest {

    @InjectMocks
    private DocumentTransformer documentTransformer;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private WillLodgementCallbackRequest wlCallbackRequest;

    @Mock
    private WillLodgementDetails wlCaseDetails;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequest;

    @Mock
    private CaveatDetails caveatDetails;

    private Document digitalGrant;

    private Document sentEmail;

    private Document coversheet;

    private Document willLodgementReceipt;

    private List<Document> documents = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        digitalGrant = Document.builder().documentType(DIGITAL_GRANT).build();
        coversheet = Document.builder().documentType(GRANT_COVERSHEET).build();
        sentEmail = Document.builder().documentType(SENT_EMAIL).build();
        willLodgementReceipt = Document.builder().documentType(WILL_LODGEMENT_DEPOSIT_RECEIPT).build();

        documents.add(digitalGrant);
        documents.add(sentEmail);
        documents.add(willLodgementReceipt);

        when(caseDetails.getData()).thenReturn(CaseData.builder().build());
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);

        when(wlCaseDetails.getData()).thenReturn(WillLodgementData.builder().build());
        when(wlCallbackRequest.getCaseDetails()).thenReturn(wlCaseDetails);

        when(caveatDetails.getData()).thenReturn(CaveatData.builder().build());
        when(caveatCallbackRequest.getCaseDetails()).thenReturn(caveatDetails);
    }

    @Test
    public void shouldHaveDocumentWithSpecifiedType() {
        assertTrue(documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT));
    }

    @Test
    public void shouldNotHaveDocumentWithSpecifiedType() {
        assertFalse(documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT_DRAFT));
    }

    @Test
    public void shouldAddDigitalGrantToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, digitalGrant, false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddSentEmailToGeneratedNotification() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, sentEmail, false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void shouldAddWillLodgementToDocumentsGenerated() {
        assertTrue(wlCallbackRequest.getCaseDetails().getData().getDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(wlCallbackRequest, willLodgementReceipt);

        assertEquals(1, wlCallbackRequest.getCaseDetails().getData().getDocumentsGenerated().size());
    }

    @Test
    public void shouldAddCoversheetToNotificationsGenerated() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, coversheet, true);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void shouldAddCoversheetToDocumentsGenerated() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, coversheet, false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddDigitalGrantDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(DIGITAL_GRANT_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddDigitalGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(DIGITAL_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddDigitalGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(DIGITAL_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddIntestacyGrantToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(INTESTACY_GRANT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddIntestacyGrantDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(INTESTACY_GRANT_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddIntestacyGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(INTESTACY_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddIntestacyGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(INTESTACY_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddAdmonWillGrantToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(ADMON_WILL_GRANT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddAdmonWillGrantDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(ADMON_WILL_GRANT_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddAdmonWillGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(ADMON_WILL_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddAdmonWillGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(ADMON_WILL_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshDigitalGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_DIGITAL_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshDigitalGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_DIGITAL_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshIntestacyGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_INTESTACY_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshIntestacyGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_INTESTACY_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshAdmonWillGrantReissueDraftToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshAdmonWillGrantReissueToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_ADMON_WILL_GRANT_REISSUE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddSOTRequestToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(SOT_INFORMATION_REQUEST).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddGrantCoverToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(GRANT_COVER).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddSentEmailToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(SENT_EMAIL).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void shouldAddCaveatStoppedToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(CAVEAT_STOPPED).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
    }

    @Test
    public void shouldNotAddEdgeCaseGeneratedDocumentsOrNotificationsGenerated() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(EDGE_CASE).build(), false);

        assertEquals(0, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
        assertEquals(0, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }


    @Test
    public void shouldAddCaveatRaisedToGeneratedDocuments() {
        assertTrue(caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(caveatCallbackRequest, Document.builder().documentType(CAVEAT_RAISED).build());

        assertEquals(1, caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().size());
    }

    @Test
    public void shouldAddCaveatCoversheetEmailToGeneratedDocuments() {
        assertTrue(caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(caveatCallbackRequest, Document.builder().documentType(CAVEAT_COVERSHEET).build());

        assertEquals(1, caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().size());
    }

    @Test
    public void shouldAddCaveatSentEmailToGeneratedDocuments() {
        assertTrue(caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(caveatCallbackRequest, Document.builder().documentType(SENT_EMAIL).build());

        assertEquals(1, caveatCallbackRequest.getCaseDetails().getData().getNotificationsGenerated().size());
    }

    @Test
    public void shouldAddSOTToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(STATEMENT_OF_TRUTH).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    public void shouldAddWelshSOTToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(WELSH_STATEMENT_OF_TRUTH).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    public void shouldAddLegalStatementGopToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(LEGAL_STATEMENT_PROBATE).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    public void shouldAddLegalStatementAdmonToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(LEGAL_STATEMENT_ADMON).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    public void shouldAddLegalStatementIntestacyToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(LEGAL_STATEMENT_INTESTACY).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateSotDocumentsGenerated().size());
    }

    @Test
    public void shouldAddAssembleLetterToGeneratedDocuments() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, Document.builder().documentType(ASSEMBLED_LETTER).build(), false);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }
}