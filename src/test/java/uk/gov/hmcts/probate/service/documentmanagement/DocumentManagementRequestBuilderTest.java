package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocumentManagementRequestBuilderTest {

    @Test
    public void shouldPrepareRequest() {
        byte[] bytes = {32, 43, 86};
        EvidenceManagementFileUpload fileUpload = new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);

        DocumentUploadRequest documentUploadRequest = DocumentManagementRequestBuilder.perpareDocumentUploadRequest(
            fileUpload, DocumentType.DIGITAL_GRANT);

        assertEquals(1, documentUploadRequest.getFiles().size());
        assertEquals("PRIVATE", documentUploadRequest.getClassification());
        assertEquals("PROBATE", documentUploadRequest.getJurisdictionId());
        assertEquals("GrantOfRepresentation", documentUploadRequest.getCaseTypeId());
        ByteArrayResource body = (ByteArrayResource) ((HttpEntity) documentUploadRequest.getFiles().get(0)).getBody();
        assertTrue(body.getFilename().contains(".pdf"));
    }
}
