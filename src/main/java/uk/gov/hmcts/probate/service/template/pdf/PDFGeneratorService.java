package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.docmosis.DocmosisPdfGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PDFGeneratorService {

    public static final String TEMPLATE_EXTENSION = ".html";
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final AppInsights appInsights;
    private final ObjectMapper objectMapper;
    private final PDFServiceClient pdfServiceClient;
    private final DocmosisPdfGenerationService docmosisPdfGenerationService;

    public EvidenceManagementFileUpload generatePdf(DocumentType documentType, String pdfGenerationData) {
        byte[] postResult;
        try {
            log.info("Generate pdf from template {}", documentType.getTemplateName());
            postResult = generateFromHtml(documentType.getTemplateName(), pdfGenerationData);
            log.info("Generated from templates with bytes size {}", postResult != null ? postResult.length : "0");
        } catch (IOException | PDFServiceClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        log.info("Returning FileUpload obj");
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, postResult);
    }

    public EvidenceManagementFileUpload generateDocmosisDocumentFrom(String templateName, Map<String, Object>
        placeholders) {
        byte[] postResult;
        try {
            postResult = docmosisPdfGenerationService.generateDocFrom(templateName, placeholders);
        } catch (PDFServiceClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, postResult);
    }

    private byte[] generateFromHtml(String templateName, String pdfGenerationData) throws IOException {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + TEMPLATE_EXTENSION;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);

        Map<String, Object> paramMap = asMap(pdfGenerationData);
        appInsights.trackEvent(REQUEST_SENT, pdfServiceConfiguration.getUrl());

        return pdfServiceClient.generateFromHtml(templateAsString.getBytes(), paramMap);
    }

    private Map<String, Object> asMap(String placeholderValues) throws IOException {
        return objectMapper.readValue(placeholderValues, new TypeReference<HashMap<String, Object>>() {
        });
    }
}
