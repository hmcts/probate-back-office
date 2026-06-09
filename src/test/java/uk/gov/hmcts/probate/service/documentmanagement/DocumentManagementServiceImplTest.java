package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.util.FileUtils;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;

class DocumentManagementServiceImplTest {
    @InjectMocks
    private DocumentManagementServiceImpl documentManagementService;

    @Mock
    private DocumentManagementRequestBuilder documentManagementRequestBuilder;
    @Mock
    private CaseDocumentClient caseDocumentClient;

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private DocumentUploadRequest documentUploadRequestMock;
    @Mock
    private UploadResponse uploadResponseMock;
    @Mock
    private ResponseEntity<Resource> getResponseMock;
    @Mock
    private Resource getBodyMock;
    @Mock
    private InputStream inputStreamMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldStoreFile() {
        EvidenceManagementFileUpload evidenceManagementFileUpload =
            new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[100]);

        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getOrDefaultCaseworkerSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getBearerToken("AUTH")).thenReturn("Bearer AUTH");
        when(documentUploadRequestMock.getCaseTypeId()).thenReturn("GrantOfRepresentation");
        when(documentUploadRequestMock.getFiles()).thenReturn(Collections.emptyList());
        when(documentUploadRequestMock.getJurisdictionId()).thenReturn("PROBATE");
        when(documentManagementRequestBuilder.perpareDocumentUploadRequest(evidenceManagementFileUpload, DIGITAL_GRANT))
            .thenReturn(documentUploadRequestMock);
        when(caseDocumentClient.uploadDocuments("Bearer AUTH", "S2S", "GrantOfRepresentation", "PROBATE",
            Collections.emptyList(), Classification.PRIVATE))
            .thenReturn(uploadResponseMock);
        UploadResponse uploadResponse = documentManagementService.upload(evidenceManagementFileUpload, DIGITAL_GRANT);

        assertEquals(uploadResponseMock, uploadResponse);
    }

    @Test
    void shouldStoreFileForCitizen() {
        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getBearerToken("AUTH")).thenReturn("Bearer AUTH");
        when(documentUploadRequestMock.getCaseTypeId()).thenReturn("GrantOfRepresentation");
        when(documentUploadRequestMock.getFiles()).thenReturn(Collections.emptyList());
        when(documentUploadRequestMock.getJurisdictionId()).thenReturn("PROBATE");

        when(caseDocumentClient.uploadDocuments("Bearer AUTH", "S2S", "GrantOfRepresentation", "PROBATE",
            Collections.emptyList(), Classification.PRIVATE))
            .thenReturn(uploadResponseMock);
        List<MultipartFile> multipartFileList = new ArrayList<>();
        when(documentManagementRequestBuilder.perpareDocumentUploadRequestForCitizen(multipartFileList, DIGITAL_GRANT))
            .thenReturn(documentUploadRequestMock);
        UploadResponse uploadResponse = documentManagementService.uploadForCitizen(multipartFileList,
            "AUTH", DIGITAL_GRANT);

        assertEquals(uploadResponseMock, uploadResponse);
    }

    @Test
    void shouldExpire() throws IOException {
        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        documentManagementService.delete(Document.builder()
            .documentLink(DocumentLink.builder()
                .documentBinaryUrl("binary-c387262a-c8a6-44eb-9aea-a740460f9302")
                .documentUrl("url-c387262a-c8a6-44eb-9aea-a740460f9302")
                .build())
            .build());
    }

    @Test
    void shoulGetDocument() throws IOException {
        when(securityUtils.getCaseworkerToken()).thenReturn("AUTH");
        when(securityUtils.generateServiceToken()).thenReturn("S2S");
        when(caseDocumentClient.getDocumentBinary(anyString(), anyString(), anyString())).thenReturn(getResponseMock);
        when(getResponseMock.getBody()).thenReturn(getBodyMock);
        File file = ResourceUtils.getFile(FileUtils.class.getResource("/" + "digitalCase.json"));
        FileInputStream fis = new FileInputStream(file);
        when(getBodyMock.getInputStream()).thenReturn(fis);
        byte[] bytes = documentManagementService.getDocument(Document.builder()
            .documentLink(DocumentLink.builder()
                .documentBinaryUrl("binary-c387262a-c8a6-44eb-9aea-a740460f9302")
                .documentUrl("url-c387262a-c8a6-44eb-9aea-a740460f9302")
                .build())
            .build());
        assertTrue(bytes.length > 0);
    }

    @Test
    void shoulThrowExceptionForNoBodyGetDocument() throws IOException {
        assertThrows(ClientException.class, () -> {
            when(securityUtils.getCaseworkerToken()).thenReturn("AUTH");
            when(securityUtils.generateServiceToken()).thenReturn("S2S");
            when(caseDocumentClient.getDocumentBinary(anyString(), anyString(), anyString()))
                    .thenReturn(getResponseMock);
            documentManagementService.getDocument(Document.builder()
                    .documentLink(DocumentLink.builder()
                            .documentBinaryUrl("binary-c387262a-c8a6-44eb-9aea-a740460f9302")
                            .documentUrl("url-c387262a-c8a6-44eb-9aea-a740460f9302")
                            .build())
                    .build());
        });
    }

    @Test
    void shouldQueryCaseDocumentClientForMetadata() {
        final String docUrl = "docUrl";
        final String auth = "auth";
        final String servAuth = "servAuth";

        final SecurityDTO secDTO = SecurityDTO.builder()
                .authorisation(auth)
                .serviceAuthorisation(servAuth)
                .build();

        when(securityUtils.getSecurityDTO()).thenReturn(secDTO);

        documentManagementService.getMetadataByUrl(docUrl);

        verify(caseDocumentClient).getMetadataForDocument(auth, servAuth, docUrl);
    }
}
