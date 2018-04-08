package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.net.URI;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PDFGeneratorServiceTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Mock
    private ByteArrayResource byteArrayResourceMock;

    @Mock
    private ClientException clientException;

    @Mock
    private ConnectionException pdfConnectionExceptionMock;

    @Mock
    private FileSystemResource fileSystemResourceMock;

    @InjectMocks
    private PDFGeneratorService underTest;

    @Before
    public void setup() {
        initMocks(this);
        when(fileSystemResourceServiceMock.getFileSystemResource(any(String.class))).thenReturn(Optional.of(fileSystemResourceMock));
    }

    @Test
    public void shouldGeneratePDFWithResponseStatusOK() {
        when(restTemplateMock.postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class)))
            .thenReturn(byteArrayResourceMock);

        underTest
            .generatePdf(PDFServiceTemplate.LEGAL_STATEMENT, "{\"data\":\"value\"}");

        verify(restTemplateMock).postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class));
    }

    @Test(expected = ClientException.class)
    public void shouldThrowPDFClientException() {
        when(restTemplateMock.postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class)))
            .thenThrow(clientException);

        underTest.generatePdf(PDFServiceTemplate.LEGAL_STATEMENT, "{\"data\":\"value\"}");
    }

    @Test(expected = ConnectionException.class)
    public void shouldThrowPDFConnectionException() {
        when(restTemplateMock.postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class)))
            .thenThrow(pdfConnectionExceptionMock);

        underTest.generatePdf(PDFServiceTemplate.LEGAL_STATEMENT, "{\"data\":\"value\"}");
    }
}
