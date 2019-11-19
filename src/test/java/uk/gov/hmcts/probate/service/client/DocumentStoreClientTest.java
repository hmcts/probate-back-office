package uk.gov.hmcts.probate.service.client;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.security.*;

import java.io.*;
import java.time.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DocumentStoreClientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private CloseableHttpClient closeableHttpClientMock;
    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @Mock
    private SecurityDTO securityDTO;

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

    @Test
    public void shouldReturnDocumentInBytesForScannedDocuments() throws IOException {
        when(closeableHttpClientMock.execute(any(HttpGet.class))).thenReturn(closeableHttpResponseMock);

        DocumentLink documentLink = DocumentLink.builder()
                .documentBinaryUrl("http://localhost")
                .build();
        ScannedDocument document = ScannedDocument.builder()
                .subtype("will")
                .url(documentLink)
                .build();
        byte[] bytes = documentStoreClient.retrieveUploadDocument(document, "", securityDTO);

        assertTrue(bytes.length > 0);
    }

    @Test
    public void shouldThrowIOExceptionForScannedDocument() throws IOException {

        doThrow(new IOException()).when(closeableHttpClientMock).execute(any(HttpGet.class));
        DocumentLink documentLink = DocumentLink.builder()
                .documentBinaryUrl("http://localhost")
                .build();
        ScannedDocument document = ScannedDocument.builder()
                .subtype("will")
                .url(documentLink)
                .build();

        expectedException.expect(IOException.class);
        expectedException.expectMessage(containsString(document.getSubtype()));

        byte[] bytes = documentStoreClient.retrieveUploadDocument(document, "", securityDTO);

        assertNull(bytes);
    }

}