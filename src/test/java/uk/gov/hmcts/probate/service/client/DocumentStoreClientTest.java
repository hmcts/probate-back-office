package uk.gov.hmcts.probate.service.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentStoreClientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private CloseableHttpClient closeableHttpClientMock;
    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @InjectMocks
    private DocumentStoreClient documentStoreClient;

    @Before
    public void setUp() throws Exception {
        HttpEntity entity = new ByteArrayEntity(new byte[2566]);
        closeableHttpResponseMock.setEntity(entity);

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

        expectedException.expect(IOException.class);
        expectedException.expectMessage(containsString(document.getDocumentFileName()));

        byte[] bytes = documentStoreClient.retrieveDocument(document, "");

        assertNull(bytes);
    }

}