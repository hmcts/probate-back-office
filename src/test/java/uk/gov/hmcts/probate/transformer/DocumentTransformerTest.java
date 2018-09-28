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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;

public class DocumentTransformerTest {

    @InjectMocks
    private DocumentTransformer documentTransformer;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private CaseDetails caseDetails;

    private Document digitalGrant;

    private Document sentEmail;

    private List<Document> documents = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        digitalGrant = Document.builder().documentType(DIGITAL_GRANT).build();
        sentEmail = Document.builder().documentType(SENT_EMAIL).build();

        documents.add(digitalGrant);
        documents.add(sentEmail);

        when(caseDetails.getData()).thenReturn(CaseData.builder().build());
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
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

        documentTransformer.addDocument(callbackRequest, digitalGrant);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated().size());
    }

    @Test
    public void shouldAddSentEmailToGeneratedNotification() {
        assertTrue(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().isEmpty());

        documentTransformer.addDocument(callbackRequest, sentEmail);

        assertEquals(1, callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated().size());
    }
}