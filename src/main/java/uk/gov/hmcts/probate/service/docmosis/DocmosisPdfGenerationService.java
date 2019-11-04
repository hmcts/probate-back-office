package uk.gov.hmcts.probate.service.docmosis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.properties.docmosis.TemplateProperties;
import uk.gov.hmcts.probate.exception.PDFGenerationException;
import uk.gov.hmcts.probate.model.docmosis.PdfDocumentRequest;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.probate.model.Constants.DOCMOSIS_OUTPUT_PDF;

@Service
@Slf4j
public class DocmosisPdfGenerationService {

    private static final String PDF_DOCUMENT_OUTPUT_NAME = "result.";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TemplateProperties templateProperties;


    @Value("${docmosis.service.uri}/rs/render")
    private String pdfServiceEndpoint;

    @Value("${docmosis.service.accessKey}")
    private String pdfServiceAccessKey;

    public byte[] generateDocFrom(String templateName, Map<String, Object> placeholders) {
        checkArgument(!isNullOrEmpty(templateName), "document generation template cannot be empty");
        checkNotNull(placeholders, "placeholders map cannot be null");

        log.info("Making request to docmosis pdf service to generate pdf document with template [{}], "
                        + "placeholders of size [{}], pdfServiceEndpoint [{}] ",
                templateName, placeholders.size(), pdfServiceEndpoint);


        try {
            ResponseEntity<byte[]> response =
                    restTemplate.postForEntity(pdfServiceEndpoint, request(templateName, placeholders), byte[]
                            .class);
            return response.getBody();
        } catch (Exception e) {
            throw new PDFGenerationException("Failed to request PDF from REST endpoint " + e.getMessage(), e);
        }
    }

    private PdfDocumentRequest request(String templateName, Map<String, Object> placeholders) {
        String docmosisTemplateName = templateProperties.getTemplates().get(templateName).getTemplateName();
        return PdfDocumentRequest.builder()
                .accessKey(pdfServiceAccessKey)
                .templateName(docmosisTemplateName)
                .outputFormat(DOCMOSIS_OUTPUT_PDF)
                .outputName(PDF_DOCUMENT_OUTPUT_NAME + DOCMOSIS_OUTPUT_PDF)
                .pdfArchiveMode(true)
                .data(placeholders).build();
    }

}
