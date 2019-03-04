package uk.gov.hmcts.probate.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.FeeServiceConfiguration;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.service.notify.NotificationClient;

@RequiredArgsConstructor
@Configuration
public class HealthConfiguration {

    private static final String HEALTH_ENDPOINT = "/health";
    private static final String STATUS_ENDPOINT = "/_status";

    private final RestTemplate restTemplate;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final FeeServiceConfiguration feeServiceConfiguration;
    private final NotificationClient notificationClient;

    @Value("${idam.service.host}")
    private String idamServiceHost;


    @Bean
    public SolsHealthIndicator idamServiceHealthIndicator() {
        return new SolsHealthIndicator(idamServiceHost, restTemplate, HEALTH_ENDPOINT);
    }

}
