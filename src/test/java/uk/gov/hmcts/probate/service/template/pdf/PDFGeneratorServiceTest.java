package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;

@ExtendWith(MockitoExtension.class)
class PDFGeneratorServiceTest {

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Mock
    private PDFServiceClientException pdfServiceClientExceptionMock;

    @Mock
    private PDFServiceClient pdfServiceClient;

    @Mock
    private DocmosisPdfGenerationService docmosisPdfGenerationServiceMock;

    @InjectMocks
    private PDFGeneratorService underTest;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        Field objectMapper = ReflectionUtils.findField(PDFGeneratorService.class, "objectMapper");
        objectMapper.setAccessible(true);
        objectMapper.set(underTest, new ObjectMapper());
    }

    @Test
    void shouldGeneratePDFWithBytesAndPDFContentType() {
        when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn("MockedBytes".getBytes());
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                .thenReturn("<htmlTemplate>");
        EvidenceManagementFileUpload result = underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
        assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        assertThat(result.getBytes().length, greaterThan(0));
    }

    @Test
    void shouldGeneratePDFFromDocmosisWithBytesAndPDFContentType() {

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
        placeholders.put("deceasedDateOfDeath", LocalDate.now().toString());

        when(docmosisPdfGenerationServiceMock.generateDocFrom(any(), anyMap()))
                .thenReturn("MockedBytes".getBytes());
        EvidenceManagementFileUpload result = underTest.generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(),
                placeholders);
        assertThat(result.getContentType(), equalTo(MediaType.APPLICATION_PDF));
        assertThat(result.getBytes().length, greaterThan(0));
    }

    @Test
    void shouldThrowClientException() {
        assertThrows(ClientException.class, () -> {
            when(pdfServiceClientExceptionMock.getMessage()).thenReturn("blah");
            when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn("MockedBytes".getBytes());
            when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                    .thenReturn("<htmlTemplate>");
            when(pdfServiceClient.generateFromHtml(any(byte[].class), anyMap()))
                    .thenThrow(pdfServiceClientExceptionMock);
            underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
        });
    }

    @Test
    void shouldThrowPDFConnectionException() {
        assertThrows(ClientException.class, () -> {
            when(pdfServiceClientExceptionMock.getMessage()).thenReturn("blah");
            when(fileSystemResourceServiceMock.getFileFromResourceAsString(anyString()))
                    .thenReturn("<htmlTemplate>");
            when(pdfServiceClient.generateFromHtml(any(), any())).thenThrow(pdfServiceClientExceptionMock);
            underTest.generatePdf(LEGAL_STATEMENT_PROBATE, "{\"data\":\"value\"}");
        });
    }

    @Test
    void shouldThrowDocmosisPDFConnectionException() {
        assertThrows(ClientException.class, () -> {
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

            when(pdfServiceClientExceptionMock.getMessage()).thenReturn("blah");
            when(docmosisPdfGenerationServiceMock.generateDocFrom(any(), any()))
                    .thenThrow(pdfServiceClientExceptionMock);
            underTest.generateDocmosisDocumentFrom(CAVEAT_RAISED.getTemplateName(), placeholders);
        });
    }
}
