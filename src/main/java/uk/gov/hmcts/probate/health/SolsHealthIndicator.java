package uk.gov.hmcts.probate.health;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

@AllArgsConstructor
public class SolsHealthIndicator implements HealthIndicator {

    private static final String EXCEPTION_KEY = "exception";
    private static final String MESSAGE_KEY = "message";
    private static final String URL_KEY = "url";
    private static final String GIT_COMMIT_ID_KEY = "gitCommitId";

    @Value("${git.commit.id}")
    private String commitId;

    private final String url;

    private final RestTemplate restTemplate;

    @Override
    public Health health() {
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url + "/health", String.class);
        } catch (ResourceAccessException rae) {
            return getHealthWithDownStatus(url, rae.getMessage(), "ResourceAccessException");
        } catch (HttpStatusCodeException hsce) {
            return getHealthWithDownStatus(url, hsce.getMessage(),
                    "HttpStatusCodeException - HTTP Status: " + hsce.getStatusCode().value());
        } catch (UnknownHttpStatusCodeException uhsce) {
            return getHealthWithDownStatus(url, uhsce.getMessage(), "UnknownHttpStatusCodeException - " + uhsce.getStatusText());
        }

        if (responseEntity != null && !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return getHealthWithDownStatus(url, "HTTP Status code not 200", "HTTP Status: " + responseEntity.getStatusCodeValue());
        }
        return Health.up()
                .withDetail(URL_KEY, url)
                .withDetail(MESSAGE_KEY, "HTTP Status OK")
                .withDetail(GIT_COMMIT_ID_KEY, this.commitId)
                .build();

        //return Health.up().withDetail(GIT_COMMIT_ID_KEY, this.commitId).build();
    }

    private Health getHealthWithDownStatus(String url, String message, String status) {
        return Health.down()
                .withDetail(URL_KEY, url)
                .withDetail(MESSAGE_KEY, message)
                .withDetail(EXCEPTION_KEY, status)
                .withDetail(GIT_COMMIT_ID_KEY, this.commitId)
                .build();
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
