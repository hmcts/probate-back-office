package uk.gov.hmcts.probate.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
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

    @Value("${git.commit.id}")
    private String commitId;

    @Bean
    public SolsHealthIndicator pdfServiceHealthIndicator() {
        return new SolsHealthIndicator(pdfServiceConfiguration.getUrl(), restTemplate);
    }

    @Bean
    public SolsHealthIndicator feeServiceHealthIndicator() {
        return new SolsHealthIndicator(feeServiceConfiguration.getUrl(), restTemplate);
    }

    @Bean
    public SolsHealthIndicator idamServiceHealthIndicator() {
        return new SolsHealthIndicator(idamServiceHost, restTemplate);
    }

    @Bean
    public SolsHealthIndicator evidenceManagementHealthIndicator() {
        return new SolsHealthIndicator(evidenceManagementHost, restTemplate);
    }

    @Bean
    public SolsHealthIndicator printServiceHealthIndicator() {
        return new SolsHealthIndicator(printServiceHost, restTemplate);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
                = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }
}
