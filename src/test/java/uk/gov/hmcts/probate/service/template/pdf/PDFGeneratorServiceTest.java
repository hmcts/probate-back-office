package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@RunWith(MockitoJUnitRunner.class)
public class PDFGeneratorServiceTest {

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Mock
    private PDFServiceClientException pdfServiceClientExceptionMock;

    @Mock
    private AppInsights appInsights;

    @Mock
    private PDFServiceClient pdfServiceClient;

    @InjectMocks
    private PDFGeneratorService underTest;

    @Before
    public void setup() throws IllegalAccessException {
        Field objectMapper = ReflectionUtils.findField(PDFGeneratorService.class, "objectMapper");
        objectMapper.setAccessible(true);
        objectMapper.set(underTest, new ObjectMapper());

        when(pdfServiceClientExceptionMock.getMessage()).thenReturn("blah");
        when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn("MockedBytes".getBytes());
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                .thenReturn("<htmlTemplate>");
    }

    @Test
    public void shouldGeneratePDFWithBytesAndPDFContentType() {
        EvidenceManagementFileUpload result = underTest.generatePdf(LEGAL_STATEMENT, "{\"data\":\"value\"}");
        Assert.assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        Assert.assertThat(result.getBytes().length, greaterThan(0));
    }

    @Test(expected = ClientException.class)
    public void shouldThrowClientException() {
        when(pdfServiceClient.generateFromHtml(any(byte[].class), anyMap())).thenThrow(pdfServiceClientExceptionMock);
        underTest.generatePdf(LEGAL_STATEMENT, "{\"data\":\"value\"}");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowPDFConnectionException() {
        when(pdfServiceClient.generateFromHtml(any(), any())).thenThrow(pdfServiceClientExceptionMock);
        underTest.generatePdf(LEGAL_STATEMENT, "{\"data\":\"value\"}");
    }
}
