package uk.gov.hmcts.probate.service.template.pdf;

import lombok.Data;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.ConnectionException;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.net.URI;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Data
@Component
public class PDFGeneratorService {

    private final RestTemplate restTemplate;
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;

    private static final String PARAMETER_TEMPLATE = "template";
    private static final String PARAMETER_PLACEHOLDER_VALUES = "placeholderValues";

    public EvidenceManagementFileUpload generatePdf(PDFServiceTemplate pdfServiceTemplate, String pdfGenerationData) {
        URI uri = URI.create(String.format("%s%s", pdfServiceConfiguration.getUrl(), pdfServiceConfiguration.getPdfApi()));

        String htmlTemplateFileName = pdfServiceTemplate.getHtmlFileName();
        HttpEntity<MultiValueMap<String, Object>> multipartRequest = createMultipartPostRequest(htmlTemplateFileName, pdfGenerationData);

        byte[] postResult;
        try {
            ByteArrayResource responseResource = restTemplate.postForObject(uri, multipartRequest, ByteArrayResource.class);
            postResult = responseResource.getByteArray();
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getStatusCode().value(), e.getMessage());
        } catch (RestClientException e) {
            throw new ConnectionException("Could not connect to PDF service");
        }

        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, postResult);
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartPostRequest(String pdfTemplateFileName, String pdfGenerationData) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + pdfTemplateFileName + ".html";

        parameters.add(PARAMETER_TEMPLATE, fileSystemResourceService.getFileSystemResource(templatePath).orElse(null));
        parameters.add(PARAMETER_PLACEHOLDER_VALUES, pdfGenerationData);

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.set(HttpHeaders.CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE);

        return new HttpEntity<>(parameters, postHeaders);
    }
}
