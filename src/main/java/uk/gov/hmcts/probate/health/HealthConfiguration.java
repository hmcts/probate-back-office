package uk.gov.hmcts.probate.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.service.notify.NotificationClient;

@Configuration
public class HealthConfiguration {

    private static final String HEALTH_ENDPOINT = "/health";
    private static final String STATUS_ENDPOINT = "/_status";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    private FeeServiceConfiguration feeServiceConfiguration;

    @Autowired
    private NotificationClient notificationClient;

    @Value("${idam.service.host}")
    private String idamServiceHost;

    @Value("${evidence.management.host}")
    private String evidenceManagementHost;

    @Value("${printservice.internal.host}")
    private String printServiceInternalHost;

    @Bean
    public SolsHealthIndicator pdfServiceHealthIndicator() {
        return new SolsHealthIndicator(pdfServiceConfiguration.getUrl(), restTemplate, HEALTH_ENDPOINT);
    }

    @Bean
    public SolsHealthIndicator feeServiceHealthIndicator() {
        return new SolsHealthIndicator(feeServiceConfiguration.getUrl(), restTemplate, HEALTH_ENDPOINT);
    }

    @Bean
    public SolsHealthIndicator idamServiceHealthIndicator() {
        return new SolsHealthIndicator(idamServiceHost, restTemplate, HEALTH_ENDPOINT);
    }

    @Bean
    public SolsHealthIndicator evidenceManagementHealthIndicator() {
        return new SolsHealthIndicator(evidenceManagementHost, restTemplate, HEALTH_ENDPOINT);
    }

    @Bean
    public SolsHealthIndicator printServiceHealthIndicator() {
        return new SolsHealthIndicator(printServiceInternalHost, restTemplate, HEALTH_ENDPOINT);
    }

    @Bean
    public SolsHealthIndicator notificationHealthIndicator() {
        return new SolsHealthIndicator(notificationClient.getBaseUrl(), restTemplate, STATUS_ENDPOINT);
    }
}
