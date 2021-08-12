package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagementRequestBuilderTest {

    @InjectMocks
    private DocumentManagementRequestBuilder documentManagementRequestBuilder;

    @Test
    public void shouldPrepareRequest() {
        byte[] bytes = {32, 43, 86};
        EvidenceManagementFileUpload fileUpload = new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);

        DocumentUploadRequest documentUploadRequest = documentManagementRequestBuilder.perpareDocumentUploadRequest(
            fileUpload, DocumentType.DIGITAL_GRANT);

        assertEquals(1, documentUploadRequest.getFiles().size());
        assertEquals("PRIVATE", documentUploadRequest.getClassification());
        assertEquals("PROBATE", documentUploadRequest.getJurisdictionId());
        assertEquals("GrantOfRepresentation", documentUploadRequest.getCaseTypeId());
        String fileName = documentUploadRequest.getFiles().get(0).getName();
        assertTrue(fileName.contains(".pdf"));
    }
}
