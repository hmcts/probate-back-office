package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import java.net.URI;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pdf.service")
public class PDFServiceConfiguration {

    private String url;
    private String pdfApi;
    private String templatesDirectory;
    private String defaultDisplayFilename;
    private String grantSignatureSecretKey;
    private String grantSignatureEncryptedFile;
    
    @Bean
    public PDFServiceClient pdfServiceClient(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            AuthTokenGenerator serviceAuthTokenGenerator) {

        URI uri = URI.create(String.format("%s%s", getUrl(), getPdfApi()));

        return PDFServiceClient.builder()
                .restOperations(restTemplate)
                .objectMapper(objectMapper)
                .build(serviceAuthTokenGenerator::generate, uri);
    }
}
