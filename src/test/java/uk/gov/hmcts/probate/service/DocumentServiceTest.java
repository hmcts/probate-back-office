package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;

@Slf4j
public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private UploadService uploadService;

    @Mock
    private CallbackRequest callbackRequest;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private Document document;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(document.getDocumentType()).thenReturn(DIGITAL_GRANT_DRAFT);
        when(document.getDocumentLink()).thenReturn(DocumentLink.builder().build());

        List<CollectionMember<Document>> documents = Arrays.asList(new CollectionMember(null, document));

        when(caseData.getProbateDocumentsGenerated()).thenReturn(documents);
        when(caseDetails.getData()).thenReturn(caseData);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
    }

    @Test
    public void shouldExpiryDocument() throws JsonProcessingException {
        doNothing().when(uploadService).expire(document);

        documentService.expire(callbackRequest, DocumentType.DIGITAL_GRANT_DRAFT);

        verify(uploadService).expire(document);
    }

    @Test
    public void shouldProduceWaringLog() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(uploadService).expire(document);

        documentService.expire(callbackRequest, DocumentType.DIGITAL_GRANT_DRAFT);

        verify(document).getDocumentLink();
    }
}