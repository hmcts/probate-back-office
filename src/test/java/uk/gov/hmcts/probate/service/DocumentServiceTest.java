package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentManagementService documentManagementService;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document document;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(document.getDocumentType()).thenReturn(DIGITAL_GRANT_DRAFT);
        when(document.getDocumentLink()).thenReturn(DocumentLink.builder().build());

        List<CollectionMember<Document>> documents = Arrays.asList(new CollectionMember(document));

        when(caseData.getProbateDocumentsGenerated()).thenReturn(documents);
        when(caseDetails.getData()).thenReturn(caseData);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
    }

    @Test
    void shouldExpireDocument() throws JsonProcessingException {
        doNothing().when(documentManagementService).delete(document);

        documentService.expire(callbackRequest, DocumentType.DIGITAL_GRANT_DRAFT);

        verify(documentManagementService).delete(document);
    }

    @Test
    void shouldProduceWaringLog() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(documentManagementService).delete(document);

        documentService.expire(callbackRequest, DocumentType.DIGITAL_GRANT_DRAFT);

        verify(document).getDocumentLink();
    }

    @Test
    void shouldDeleteDocument() throws JsonProcessingException {
        Document document = Document.builder().build();

        documentService.delete(document, "99");

        verify(documentManagementService).delete(document);
    }

    @Test
    void shouldNotDeleteDocumentWhenThrowingException() throws JsonProcessingException {
        Document document = Document.builder().build();

        doThrow(new RuntimeException("")).when(documentManagementService).delete(document);
        documentService.delete(document, "99");
    }
}
