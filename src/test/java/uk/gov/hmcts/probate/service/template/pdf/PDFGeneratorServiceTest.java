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
import uk.gov.hmcts.probate.service.docmosis.DocmosisPdfGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;

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

    @Test
    public void convertDateInWelsh(){
        final LocalDate localDate =  LocalDate.of(2019,5,15);
        final String dateInWelsh =  underTest.convertDateInWelsh(localDate);
        Assert.assertEquals("15 Mai 2019","15 Mai 2019");
    }
}
