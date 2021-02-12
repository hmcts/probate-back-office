package uk.gov.hmcts.probate.service.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentStoreClientTest {
    private DocumentStoreClient documentStoreClient;

    @Mock
    private CloseableHttpClient closeableHttpClientMock;
    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @Before
    public void setUp() {
        documentStoreClient = new DocumentStoreClient(securityUtils);
        documentStoreClient.closeableHttpClient = closeableHttpClientMock;

        HttpEntity entity = new ByteArrayEntity(new byte[2566]);

        when(closeableHttpResponseMock.getEntity()).thenReturn(entity);
    }

    @Test
    public void shouldReturnDocumentInBytes() throws IOException {
        when(closeableHttpClientMock.execute(any(HttpGet.class))).thenReturn(closeableHttpResponseMock);

        DocumentLink documentLink = DocumentLink.builder()
            .documentBinaryUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();
        byte[] bytes = documentStoreClient.retrieveDocument(document, "");

        assertTrue(bytes.length > 0);
    }

    @Test
    public void shouldReturnDocumentInBytesWithNotDocUserId() throws IOException {
        when(closeableHttpClientMock.execute(any(HttpGet.class))).thenReturn(closeableHttpResponseMock);
        when(securityUtils.getSecurityDTO()).thenReturn(SecurityDTO.builder().userId("user1").build());

        DocumentLink documentLink = DocumentLink.builder()
            .documentBinaryUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        byte[] bytes = documentStoreClient.retrieveDocument(document, "");

        assertTrue(bytes.length > 0);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOException() throws IOException {

        doThrow(new IOException()).when(closeableHttpClientMock).execute(any(HttpGet.class));
        DocumentLink documentLink = DocumentLink.builder()
            .documentBinaryUrl("http://localhost")
            .build();
        Document document = Document.builder()
            .documentFileName("test.pdf")
            .documentGeneratedBy("test")
            .documentDateAdded(LocalDate.now())
            .documentLink(documentLink)
            .build();

        byte[] bytes = documentStoreClient.retrieveDocument(document, "");

        assertNull(bytes);
    }

}