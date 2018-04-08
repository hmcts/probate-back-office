package uk.gov.hmcts.probate.service.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.controller.exception.PDFClientException;
import uk.gov.hmcts.probate.model.pdf.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.net.URI;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Component
public class PDFGeneratorService {

    @Value("${pdf.service.url}")
    private String pdfServiceUrl;

    @Value("${pdf.service.pdfApi}")
    private String pdfServicePdfApi;

    @Value("${pdf.service.templatesDirectory}")
    private String templatesDirectory;

    private final RestTemplate restTemplate;
    private final FileSystemResourceService fileSystemResourceService;

    private static final String PARAMETER_TEMPLATE = "template";
    private static final String PARAMETER_PLACEHOLDER_VALUES = "placeholderValues";

    @Autowired
    PDFGeneratorService(RestTemplate restTemplate, FileSystemResourceService fileSystemResourceService) {
        this.restTemplate = restTemplate;
        this.fileSystemResourceService = fileSystemResourceService;
    }

    public ResponseEntity<byte[]> generatePdf(PDFServiceTemplate pdfServiceTemplate, String pdfGenerationData) {
        URI uri = URI.create(String.format("%s%s", pdfServiceUrl, pdfServicePdfApi));

        String htmlTemplateFileName = pdfServiceTemplate.getHtmlFileName();
        HttpEntity<MultiValueMap<String, Object>> multipartRequest = createMultipartPostRequest(htmlTemplateFileName, pdfGenerationData);

        byte[] postResult = {};
        try {
            ByteArrayResource responseResource = restTemplate.postForObject(uri, multipartRequest, ByteArrayResource.class);
            postResult = responseResource.getByteArray();
        } catch (HttpClientErrorException e) {
            throw new PDFClientException(e);
        }
        return createPDFResponse(htmlTemplateFileName, postResult);
    }

    private ResponseEntity<byte[]> createPDFResponse(String pdfTemplateFileName, byte[] postResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APPLICATION_PDF_VALUE));
        headers.setContentDispositionFormData("file", pdfTemplateFileName + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(postResult, headers, OK);
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartPostRequest(String pdfTemplateFileName, String pdfGenerationData) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add(PARAMETER_TEMPLATE, fileSystemResourceService
            .getFileSystemResource(templatesDirectory + pdfTemplateFileName + ".html"));
        parameters.add(PARAMETER_PLACEHOLDER_VALUES, pdfGenerationData);
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.set("Content-Type", MULTIPART_FORM_DATA_VALUE);
        return new HttpEntity<>(parameters, postHeaders);
    }

}
