package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
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

    private Document digitalGrant;

    private Document sentEmail;

    private Document willLodgementReceipt;

    private List<Document> documents = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        digitalGrant = Document.builder().documentType(DIGITAL_GRANT).build();
        sentEmail = Document.builder().documentType(SENT_EMAIL).build();
        willLodgementReceipt = Document.builder().documentType(WILL_LODGEMENT_DEPOSIT_RECEIPT).build();

        documents.add(digitalGrant);
        documents.add(sentEmail);
        documents.add(willLodgementReceipt);

        when(caseDetails.getData()).thenReturn(CaseData.builder().build());
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);

        when(wlCaseDetails.getData()).thenReturn(WillLodgementData.builder().build());
        when(wlCallbackRequest.getCaseDetails()).thenReturn(wlCaseDetails);
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
}