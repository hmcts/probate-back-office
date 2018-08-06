package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("pdf.service")
public class PDFServiceConfiguration {

    private String url;
    private String pdfApi;
    private String templatesDirectory;
    private String defaultDisplayFilename;
    private String grantSignatureBase64;
}
