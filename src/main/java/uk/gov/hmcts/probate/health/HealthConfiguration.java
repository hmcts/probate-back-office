package uk.gov.hmcts.probate.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;

@Configuration
public class HealthConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    private FeeServiceConfiguration feeServiceConfiguration;

    @Value("${idam.service.host}")
    private String idamServiceHost;

    @Value("${evidence.management.host}")
    private String evidenceManagementHost;

    @Value("${printservice.host}")
    private String printServiceHost;

    @Bean
    public ExternalEndpointHealthIndicator pdfServiceHealthIndicator() {
        return new ExternalEndpointHealthIndicator(pdfServiceConfiguration.getUrl(), restTemplate);
    }

    @Bean
    public ExternalEndpointHealthIndicator feeServiceHealthIndicator() {
        return new ExternalEndpointHealthIndicator(feeServiceConfiguration.getUrl(), restTemplate);
    }

    @Bean
    public ExternalEndpointHealthIndicator idamServiceHealthIndicator() {
        return new ExternalEndpointHealthIndicator(idamServiceHost, restTemplate);
    }

    @Bean
    public ExternalEndpointHealthIndicator evidenceManagementHealthIndicator() {
        return new ExternalEndpointHealthIndicator(evidenceManagementHost, restTemplate);
    }

    @Bean
    public ExternalEndpointHealthIndicator printServiceHealthIndicator() {
        return new ExternalEndpointHealthIndicator(printServiceHost, restTemplate);
    }
}
