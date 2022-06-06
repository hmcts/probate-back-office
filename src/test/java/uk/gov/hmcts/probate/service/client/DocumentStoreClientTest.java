package uk.gov.hmcts.probate.service.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DocumentStoreClientTest {

    @Mock
    private CloseableHttpClient closeableHttpClientMock;
    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @InjectMocks
    private DocumentStoreClient documentStoreClient;

    @BeforeEach
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
    public void shouldThrowIOException() {
        IOException e = assertThrows(IOException.class, () -> {
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
        });
        assertThat(e.getMessage(), containsString("test.pdf"));
    }

}
