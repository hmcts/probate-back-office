package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@RunWith(MockitoJUnitRunner.class)
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

        when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn("MockedBytes".getBytes());
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                .thenReturn("<htmlTemplate>");
    }

    @Test
    public void shouldGeneratePDFWithResponseStatusOK() {
        EvidenceManagementFileUpload result = underTest.generatePdf(LEGAL_STATEMENT, "{\"data\":\"value\"}");
        Assert.assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
    }
}
