package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.reform.sendletter.api.SendLetterResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReprintServiceTest {

    @InjectMocks
    ReprintService reprintService;

    @Mock
    private BulkPrintService bulkPrintService;
    @Mock
    private PDFManagementService pdfManagementService;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;

    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;

    @Captor
    private ArgumentCaptor<Document> selectedDocumentCaptor;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getData()).thenReturn(caseData);
    }

    @Test
    public void shouldReprintSelectedGrantDocument() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("GrantFileName")
                .label("Grant")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);
        when(caseData.getReprintNumberOfCopies()).thenReturn("10");

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupGeneratedDocs();

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendDocumentsForReprint(any(), any(), any())).thenReturn(sendLetterResponse);
        reprintService.reprintSelectedDocument(callbackRequest);

        verify(bulkPrintService).sendDocumentsForReprint(any(), selectedDocumentCaptor.capture(), any());
        assertThat(selectedDocumentCaptor.getValue().getDocumentType(), is(DocumentType.DIGITAL_GRANT));
        assertThat(selectedDocumentCaptor.getValue().getDocumentFileName(), is("GrantFileName"));
    }

    @Test
    public void shouldReprintSelectedGrantDocumentNoLetterId() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("GrantFileName")
                .label("Grant")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);
        when(caseData.getReprintNumberOfCopies()).thenReturn("10");

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupGeneratedDocs();

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendDocumentsForReprint(any(), any(), any())).thenReturn(sendLetterResponse);
        reprintService.reprintSelectedDocument(callbackRequest);

        verify(bulkPrintService).sendDocumentsForReprint(any(), selectedDocumentCaptor.capture(), any());
        assertThat(selectedDocumentCaptor.getValue().getDocumentType(), is(DocumentType.DIGITAL_GRANT));
        assertThat(selectedDocumentCaptor.getValue().getDocumentFileName(), is("GrantFileName"));
    }

    @Test
    public void shouldReprintSelectedReissuedGrantDocument() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("ReissuedGrantFileName")
                .label("ReissuedGrant")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);
        when(caseData.getReprintNumberOfCopies()).thenReturn("10");

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupGeneratedDocs();

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendDocumentsForReprint(any(), any(), any())).thenReturn(sendLetterResponse);
        reprintService.reprintSelectedDocument(callbackRequest);

        verify(bulkPrintService).sendDocumentsForReprint(any(), selectedDocumentCaptor.capture(), any());
        assertThat(selectedDocumentCaptor.getValue().getDocumentType(), is(DocumentType.DIGITAL_GRANT_REISSUE));
        assertThat(selectedDocumentCaptor.getValue().getDocumentFileName(), is("ReissuedGrantFileName"));
    }

    @Test
    public void shouldReprintSelectedSOTDocument() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("SOTFileName")
                .label("SOT")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);
        when(caseData.getReprintNumberOfCopies()).thenReturn("10");

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupSOTDoc();

        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendDocumentsForReprint(any(), any(), any())).thenReturn(sendLetterResponse);
        reprintService.reprintSelectedDocument(callbackRequest);

        verify(bulkPrintService).sendDocumentsForReprint(any(), selectedDocumentCaptor.capture(), any());
        assertThat(selectedDocumentCaptor.getValue().getDocumentType(), is(DocumentType.STATEMENT_OF_TRUTH));
        assertThat(selectedDocumentCaptor.getValue().getDocumentFileName(), is("SOTFileName"));
    }

    @Test
    public void shouldReprintSelectedWillDocument() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("WillFileName")
                .label("Will")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);
        when(caseData.getReprintNumberOfCopies()).thenReturn("10");

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupScannedDocs();
        SendLetterResponse sendLetterResponse = new SendLetterResponse(UUID.randomUUID());
        when(bulkPrintService.sendDocumentsForReprint(any(), any(), any())).thenReturn(sendLetterResponse);

        reprintService.reprintSelectedDocument(callbackRequest);

        verify(bulkPrintService).sendDocumentsForReprint(any(), selectedDocumentCaptor.capture(), any());
        assertThat(selectedDocumentCaptor.getValue().getDocumentType(), is(DocumentType.OTHER));
        assertThat(selectedDocumentCaptor.getValue().getDocumentFileName(), is("WillFileName"));
    }

    @Test(expected = BadRequestException.class)
    public void shouldThowExceptionForNoSelection() {
        DynamicList doc = DynamicList.builder()
            .build();
        when(caseData.getReprintDocument()).thenReturn(doc);

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThowExceptionForNoLabelSelection() {
        DynamicList doc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("GrantFileName")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(doc);

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThowExceptionForNoCodeSelection() {
        DynamicList doc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .label("Grant")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(doc);

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowExceptionForUnknownDocType() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("OtherFileName")
                .label("Other")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupGeneratedDocs();

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotReprintSelectedGrantDocumentWhenFileNamesDontMatch() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("GrantFileNameXXX")
                .label("Grant")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupGeneratedDocs();

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotReprintSelectedWillDocumentForFileNameMismatch() {
        DynamicList reprintDoc = DynamicList.builder()
            .value(DynamicListItem.builder()
                .code("WillFileNameXXX")
                .label("Will")
                .build())
            .build();
        when(caseData.getReprintDocument()).thenReturn(reprintDoc);

        Document coversheet = Document.builder().build();
        when(pdfManagementService.generateAndUpload(any(CallbackRequest.class), any(DocumentType.class)))
            .thenReturn(coversheet);

        setupScannedDocs();

        reprintService.reprintSelectedDocument(callbackRequest);
    }

    private void setupSOTDoc() {
        List<CollectionMember<Document>> collectionMemberList = new ArrayList();
        Document sot = Document.builder()
            .documentType(DocumentType.STATEMENT_OF_TRUTH)
            .documentFileName("SOTFileName")
            .build();

        CollectionMember<Document> collectionMember1 = new CollectionMember(null, sot);
        collectionMemberList.add(collectionMember1);

        when(caseData.getProbateSotDocumentsGenerated()).thenReturn(collectionMemberList);
    }

    private void setupGeneratedDocs() {
        List<CollectionMember<Document>> collectionMemberList = new ArrayList();
        Document grant = Document.builder()
            .documentType(DocumentType.DIGITAL_GRANT)
            .documentFileName("GrantFileName")
            .build();
        Document reissuedGrant = Document.builder()
            .documentType(DocumentType.DIGITAL_GRANT_REISSUE)
            .documentFileName("ReissuedGrantFileName")
            .build();
        CollectionMember<Document> collectionMember1 = new CollectionMember(null, grant);
        collectionMemberList.add(collectionMember1);
        CollectionMember<Document> collectionMember3 = new CollectionMember(null, reissuedGrant);
        collectionMemberList.add(collectionMember3);
        when(caseData.getProbateDocumentsGenerated()).thenReturn(collectionMemberList);
    }

    private void setupScannedDocs() {
        List<CollectionMember<ScannedDocument>> collectionMemberList = new ArrayList();
        ScannedDocument will = ScannedDocument.builder()
            .type("Other")
            .subtype("will")
            .fileName("WillFileName")
            .build();
        CollectionMember<ScannedDocument> collectionMember1 = new CollectionMember(null, will);
        collectionMemberList.add(collectionMember1);
        when(caseData.getScannedDocuments()).thenReturn(collectionMemberList);

    }
}