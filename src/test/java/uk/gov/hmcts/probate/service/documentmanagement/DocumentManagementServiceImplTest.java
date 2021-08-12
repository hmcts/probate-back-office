package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.builder.DocumentManagementURIBuilder;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagementServiceImplTest {

    private static final String URL = "URL";

    @InjectMocks
    private DocumentManagementServiceImpl emUploadService;

    @Mock
    private DocumentManagementURIBuilder documentManagementURIBuilder;
    @Mock
    private CaseDocumentClient caseDocumentClient;
    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private UploadResponse uploadResponseMock;

    @Test
    public void shouldStoreFile() throws Exception {
        EvidenceManagementFile evidenceManagementFile = new EvidenceManagementFile();
        evidenceManagementFile.setDocumentType("TEST_DOCUMENT_TYPE");
        evidenceManagementFile.setSize(200L);
        evidenceManagementFile.setOriginalDocumentName("ORIGINAL_DOCUMENT_NAME");
        evidenceManagementFile.setCreatedBy("TEST_USER");
        evidenceManagementFile.setLastModifiedBy("TEST_USER");
        evidenceManagementFile.setModifiedOn(new Date());
        evidenceManagementFile.setCreatedOn(new Date());
        evidenceManagementFile.setMimeType("mime type");
        evidenceManagementFile.setLinks(new HashMap<>());

        when(documentManagementURIBuilder.buildUrl()).thenReturn(URL);
        EvidenceManagementFileUpload evidenceManagementFileUpload =
            new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[100]);

        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseDocumentClient.uploadDocuments(any(), any(), any(), any(), any(), any()))
            .thenReturn(uploadResponseMock);
        UploadResponse uploadResponse = emUploadService.store(evidenceManagementFileUpload, DIGITAL_GRANT);

        assertEquals(uploadResponseMock, uploadResponse);
        assertThat(uploadResponse.getDocuments().get(0), equalTo(evidenceManagementFile));
        verify(documentManagementURIBuilder).buildUrl();
    }

    public void testExpire() throws IOException {
        SecurityDTO securityDTO = SecurityDTO.builder()
            .authorisation("AUTH")
            .serviceAuthorisation("S2S")
            .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        emUploadService.expire(Document.builder()
            .documentLink(DocumentLink.builder()
                .documentBinaryUrl("binary")
                .documentUrl("url")
                .build())
            .build());

        verify(caseDocumentClient).deleteDocument("AUTH", "S2S", UUID.fromString("url"), true);
    }
}