package uk.gov.hmcts.probate.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;

@Configuration
public class HealthConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Bean
    public SolsHealthIndicator pdfServiceHealthIndicator() {
        return new SolsHealthIndicator(pdfServiceConfiguration.getUrl(), restTemplate);
    }

}
