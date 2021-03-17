package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import uk.gov.hmcts.probate.config.EvidenceManagementRestTemplate;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.evidencemanagement.builder.DocumentManagementURIBuilder;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmUploadServiceTest {

    private static final String URL = "URL";

    @InjectMocks
    private EmUploadService emUploadService;

    @Mock
    private EvidenceManagementRestTemplate evidenceManagementRestTemplate;

    @Mock
    private DocumentManagementURIBuilder documentManagementURIBuilder;

    @Mock
    private HttpHeadersFactory httpHeadersFactory;

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

        HashMap embedded = new HashMap();
        embedded.put("documents", Collections.singletonList(evidenceManagementFile));
        HashMap response = new HashMap();
        response.put("_embedded", embedded);

        when(documentManagementURIBuilder.buildUrl()).thenReturn(URL);
        when(evidenceManagementRestTemplate.postForObject(eq(URL), any(), eq(HashMap.class))).thenReturn(response);
        EvidenceManagementFileUpload evidenceManagementFileUpload =
                new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[100]);

        EvidenceManagementFile actualEvidenceManagementFile = emUploadService.store(evidenceManagementFileUpload);

        assertThat(actualEvidenceManagementFile, equalTo(evidenceManagementFile));
        verify(evidenceManagementRestTemplate).postForObject(eq(URL), any(), eq(HashMap.class));
        verify(httpHeadersFactory).getMultiPartHttpHeader();
        verify(documentManagementURIBuilder).buildUrl();
    }

    @Test(expected = ClientDataException.class)
    public void testExceptionWithNullFromApiCall() throws IOException {
        when(documentManagementURIBuilder.buildUrl()).thenReturn(URL);
        when(evidenceManagementRestTemplate.postForObject(eq(URL), any(), eq(HashMap.class))).thenReturn(null);
        EvidenceManagementFileUpload evidenceManagementFileUpload =
            new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, new byte[100]);

        emUploadService.store(evidenceManagementFileUpload);
    }
}