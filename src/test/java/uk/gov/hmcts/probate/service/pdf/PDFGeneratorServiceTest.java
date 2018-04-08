package uk.gov.hmcts.probate.service.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.controller.exception.PDFClientException;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PDFGeneratorServiceTest {

    @InjectMocks
    private PDFGeneratorService pdfGeneratorService;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private ByteArrayResource byteArrayResourceMock;

    @Mock
    PDFClientException pdfClientExceptionMock;

    @Mock
    FileSystemResource fileSystemResourceMock;

    @Before
    public void setup() {
        initMocks(this);

        when(fileSystemResourceServiceMock.getFileSystemResource(any(String.class))).thenReturn(fileSystemResourceMock);
    }

    @Test
    public void shouldGeneratePDFWithResponseStatusOK() throws IOException {
        when(restTemplateMock.postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class)))
            .thenReturn(byteArrayResourceMock);
        ResponseEntity<byte[]> response = pdfGeneratorService.generatePdf(PDFServiceTemplate.LEGAL_STATEMENT, "{\"data\":\"value\"}");

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldThrowPDFClientExcption() throws IOException {
        when(restTemplateMock.postForObject(any(URI.class), any(HttpEntity.class), eq(ByteArrayResource.class)))
            .thenThrow(pdfClientExceptionMock);

        PDFClientException pdfClientException = null;
        try {
            ResponseEntity<byte[]> response = pdfGeneratorService.generatePdf(PDFServiceTemplate.LEGAL_STATEMENT, "{\"data\":\"value\"}");
        } catch (PDFClientException e) {
            pdfClientException = e;
        }

        assertThat(pdfClientException, is(pdfClientExceptionMock));
    }

}
