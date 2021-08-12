package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagementServiceImplTest {
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

    @Test
    public void shouldStoreFile() throws Exception {
        EvidenceManagementFileUpload evidenceManagementFileUpload =
            new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[100]);

        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(documentUploadRequestMock.getCaseTypeId()).thenReturn("GrantOfRepresentation");
        when(documentUploadRequestMock.getFiles()).thenReturn(Collections.emptyList());
        when(documentUploadRequestMock.getJurisdictionId()).thenReturn("PROBATE");

        when(documentManagementRequestBuilder.perpareDocumentUploadRequest(evidenceManagementFileUpload, DIGITAL_GRANT))
            .thenReturn(documentUploadRequestMock);
        when(caseDocumentClient.uploadDocuments("AUTH", "S2S", "GrantOfRepresentation", "PROBATE",
            Collections.emptyList(), Classification.PRIVATE))
            .thenReturn(uploadResponseMock);
        UploadResponse uploadResponse = documentManagementService.store(evidenceManagementFileUpload, DIGITAL_GRANT);

        assertEquals(uploadResponseMock, uploadResponse);
    }

    @Test
    public void shouldExpire() throws IOException {
        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        documentManagementService.expire(Document.builder()
            .documentLink(DocumentLink.builder()
                .documentBinaryUrl("binary-c387262a-c8a6-44eb-9aea-a740460f9302")
                .documentUrl("url-c387262a-c8a6-44eb-9aea-a740460f9302")
                .build())
            .build());
    }
}