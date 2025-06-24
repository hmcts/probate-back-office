package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.docmosis.DocmosisPdfGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import uk.gov.hmcts.probate.commons.service.PdfTemplateService;
import uk.gov.hmcts.reform.probate.exception.ProbateRuntimeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
public class PDFGeneratorService {

    public static final String TEMPLATE_EXTENSION = ".peb";

    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final ObjectMapper objectMapper;
    private final PDFServiceClient pdfServiceClient;
    private final DocmosisPdfGenerationService docmosisPdfGenerationService;
    private final PdfTemplateService pdfTemplateService;
    private final FeatureToggleService featureToggleService;

    public PDFGeneratorService(
            final FileSystemResourceService fileSystemResourceService,
            final PDFServiceConfiguration pdfServiceConfiguration,
            final ObjectMapper objectMapper,
            final PDFServiceClient pdfServiceClient,
            final DocmosisPdfGenerationService docmosisPdfGenerationService,
            final PdfTemplateService pdfTemplateService,
            final FeatureToggleService featureToggleService) {
        this.fileSystemResourceService = fileSystemResourceService;
        this.pdfServiceConfiguration = pdfServiceConfiguration;
        this.objectMapper = objectMapper;
        this.pdfServiceClient = pdfServiceClient;
        this.docmosisPdfGenerationService = docmosisPdfGenerationService;
        this.pdfTemplateService = pdfTemplateService;
        this.featureToggleService = featureToggleService;
    }

    public EvidenceManagementFileUpload generatePdf(DocumentType documentType, String pdfGenerationData) {
        final byte[] postResult;

        final Map<String, Object> params;
        try {
            params = asMap(pdfGenerationData);
        } catch (IOException e) {
            log.error("Unable to convert pdf parameter string to json", e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        try {
            if (featureToggleService.useCommonsPdfGen()) {

                final Optional<String> commonsTemplate = documentType.getCommonsTemplateName();

                log.info("Generating document using commons templating: {}", commonsTemplate);
                postResult = pdfTemplateService.generate(
                        commonsTemplate.orElseThrow(),
                        Locale.ENGLISH,
                        params);
            } else {
                log.info("Falling back to old document generation process for {}", documentType.name());
                postResult = generateFromHtml(documentType.getCommonsTemplateName().orElseThrow(), params);
            }
        } catch (ProbateRuntimeException | PDFServiceClientException | NoSuchElementException e) {
            log.error("Unable to generate pdf", e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        log.info(
                "Generated from template with size: {} bytes",
                postResult != null ? postResult.length : "null_array");
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

    private byte[] generateFromHtml(
            final String templateName,
            final Map<String, Object> params) {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + TEMPLATE_EXTENSION;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);
        return pdfServiceClient.generateFromHtml(templateAsString.getBytes(), params);
    }

    private Map<String, Object> asMap(String placeholderValues) throws IOException {
        return objectMapper.readValue(placeholderValues, new TypeReference<HashMap<String, Object>>() {
        });
    }
}
