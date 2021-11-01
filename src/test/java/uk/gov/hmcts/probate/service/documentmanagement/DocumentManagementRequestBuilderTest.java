package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagementRequestBuilderTest {

    @InjectMocks
    private DocumentManagementRequestBuilder documentManagementRequestBuilder;

    @Test
    public void shouldPrepareRequestForGrantDoc() {
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
    
    @Test
    public void shouldPrepareRequestForCaveatDoc() {
        byte[] bytes = {32, 43, 86};
        EvidenceManagementFileUpload fileUpload = new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);

        DocumentUploadRequest documentUploadRequest = documentManagementRequestBuilder.perpareDocumentUploadRequest(
            fileUpload, DocumentType.CAVEAT_COVERSHEET);

        assertEquals(1, documentUploadRequest.getFiles().size());
        assertEquals("PRIVATE", documentUploadRequest.getClassification());
        assertEquals("PROBATE", documentUploadRequest.getJurisdictionId());
        assertEquals("Caveat", documentUploadRequest.getCaseTypeId());
        String fileName = documentUploadRequest.getFiles().get(0).getName();
        assertTrue(fileName.contains(".pdf"));
    }

    @Test
    public void shouldPrepareRequestForWillDoc() {
        byte[] bytes = {32, 43, 86};
        EvidenceManagementFileUpload fileUpload = new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);

        DocumentUploadRequest documentUploadRequest = documentManagementRequestBuilder.perpareDocumentUploadRequest(
            fileUpload, DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT);

        assertEquals(1, documentUploadRequest.getFiles().size());
        assertEquals("PRIVATE", documentUploadRequest.getClassification());
        assertEquals("PROBATE", documentUploadRequest.getJurisdictionId());
        assertEquals("WillLodgement", documentUploadRequest.getCaseTypeId());
        String fileName = documentUploadRequest.getFiles().get(0).getName();
        assertTrue(fileName.contains(".pdf"));
    }

    @Test
    public void shouldPrepareRequestForCitizenDoc() {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        DocumentUploadRequest documentUploadRequest = documentManagementRequestBuilder.perpareDocumentUploadRequestForCitizen(
            multipartFileList, DocumentType.WILL_LODGEMENT_DEPOSIT_RECEIPT);

        assertEquals(0, documentUploadRequest.getFiles().size());
        assertEquals("PRIVATE", documentUploadRequest.getClassification());
        assertEquals("PROBATE", documentUploadRequest.getJurisdictionId());
        assertEquals("WillLodgement", documentUploadRequest.getCaseTypeId());
    }
}