package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.*;
import org.junit.Assert;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.http.*;
import org.springframework.util.*;
import uk.gov.hmcts.probate.config.*;
import uk.gov.hmcts.probate.exception.*;
import uk.gov.hmcts.probate.insights.*;
import uk.gov.hmcts.probate.model.evidencemanagement.*;
import uk.gov.hmcts.probate.service.*;
import uk.gov.hmcts.probate.service.docmosis.*;
import uk.gov.hmcts.reform.pdf.service.client.*;
import uk.gov.hmcts.reform.pdf.service.client.exception.*;

import java.lang.reflect.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.probate.model.DocumentType.*;

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

    @Mock
    private DocmosisPdfGenerationService docmosisPdfGenerationServiceMock;

    @InjectMocks
    private PDFGeneratorService underTest;

    @Before
    public void setup() throws IllegalAccessException {
        Field objectMapper = ReflectionUtils.findField(PDFGeneratorService.class, "objectMapper");
        objectMapper.setAccessible(true);
        objectMapper.set(underTest, new ObjectMapper());

        when(pdfServiceClientExceptionMock.getMessage()).thenReturn("blah");
        when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn("MockedBytes".getBytes());
        when(docmosisPdfGenerationServiceMock.generateDocFrom(any(), anyMap()))
                .thenReturn("MockedBytes".getBytes());
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                .thenReturn("<htmlTemplate>");
    }

    @Test
    public void shouldGeneratePDFWithBytesAndPDFContentType() {
        EvidenceManagementFileUpload result = underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
        Assert.assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        Assert.assertThat(result.getBytes().length, greaterThan(0));
    }


    @Test
    public void shouldGeneratePDFFromDocmosisWithBytesAndPDFContentType() {

        Map<String, Object> registry =  new HashMap<>();
        registry.put("name", "Bristol District Probate Registry");
        registry.put("phone", "02920 474373");
        registry.put("emailReplyToId", "6d98cad6-adb4-4446-b37e-5c3f0441a0c8");
        registry.put("addressLine1", "3rd Floor, Cardiff Magistrates’ Court");
        registry.put("addressLine2", "Fitzalan Place");
        registry.put("addressLine3", "Cardiff");
        registry.put("addressLine4", "");
        registry.put("town", "South Wales");
        registry.put("postcode", "CF24 0RZ");

        Map<String, Object> placeholders =  new HashMap<>();
        placeholders.put("caseReference", "1111-2222-3333-4444");
        placeholders.put("generatedDate", "13052019");
        placeholders.put("registry", registry);
        placeholders.put("PA8AURL", "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        placeholders.put("hmctsfamily", "image:base64:" + null);

        EvidenceManagementFileUpload result = underTest.generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(),
                placeholders);
        Assert.assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        Assert.assertThat(result.getBytes().length, greaterThan(0));
    }

    @Test
    public void shouldUploadDocument() {

        byte [] bytes = "a string".getBytes();

        EvidenceManagementFileUpload result = underTest.uploadDocument(bytes);
        Assert.assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        Assert.assertThat(result.getBytes().length, greaterThan(0));
    }

    @Test(expected = ClientException.class)
    public void shouldThrowClientException() {
        when(pdfServiceClient.generateFromHtml(any(byte[].class), anyMap())).thenThrow(pdfServiceClientExceptionMock);
        underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowPDFConnectionException() {
        when(pdfServiceClient.generateFromHtml(any(), any())).thenThrow(pdfServiceClientExceptionMock);
        underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
    }

    @Test(expected = ClientException.class)
    public void shouldThrowDocmosisPDFConnectionException() {

        Map<String, Object> registry =  new HashMap<>();
        registry.put("name", "Bristol District Probate Registry");
        registry.put("phone", "02920 474373");
        registry.put("emailReplyToId", "6d98cad6-adb4-4446-b37e-5c3f0441a0c8");
        registry.put("addressLine1", "3rd Floor, Cardiff Magistrates’ Court");
        registry.put("addressLine2", "Fitzalan Place");
        registry.put("addressLine3", "Cardiff");
        registry.put("addressLine4", "");
        registry.put("town", "South Wales");
        registry.put("postcode", "CF24 0RZ");

        Map<String, Object> placeholders =  new HashMap<>();
        placeholders.put("caseReference", "1111-2222-3333-4444");
        placeholders.put("generatedDate", "13052019");
        placeholders.put("registry", registry);
        placeholders.put("PA8AURL", "www.citizensadvice.org.uk|https://www.citizensadvice.org.uk/");
        placeholders.put("hmctsfamily", "image:base64:" + null);

        when(docmosisPdfGenerationServiceMock.generateDocFrom(any(), any()))
                .thenThrow(pdfServiceClientExceptionMock);
        underTest.generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(), placeholders);
    }
}
