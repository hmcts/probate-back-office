package uk.gov.hmcts.probate.health;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

@AllArgsConstructor
public class SolsHealthIndicator implements HealthIndicator {

    public static final String EXCEPTION_KEY = "exception";
    public static final String MESSAGE_KEY = "message";
    public static final String URL_KEY = "url";

    private final String url;

    private final RestTemplate restTemplate;

    @Override
    public Health health() {
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url + "/health", String.class);
        } catch (ResourceAccessException rae) {
            return getOutOfServiceHealth(url, rae.getMessage(), "ResourceAccessException");
        } catch (HttpStatusCodeException hsce) {
            return getOutOfServiceHealth(url, hsce.getMessage(), "HttpStatusCodeException - HTTP Status: " + hsce.getStatusCode().value());
        } catch (UnknownHttpStatusCodeException uhsce) {
            return getOutOfServiceHealth(url, uhsce.getMessage(), "UnknownHttpStatusCodeException - " + uhsce.getStatusText());
        }

        if (responseEntity != null && !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return getOutOfServiceHealth(url, "HTTP Status code not 200", "HTTP Status: " + responseEntity.getStatusCodeValue());
        }
        return Health.up()
                .withDetail(URL_KEY, url)
                .withDetail(MESSAGE_KEY, "HTTP Status OK")
                .build();
    }

    public Health getOutOfServiceHealth(String url, String message, String status) {
        return Health.outOfService()
                .withDetail(URL_KEY, url)
                .withDetail(MESSAGE_KEY, message)
                .withDetail(EXCEPTION_KEY, status)
                .build();
    }
}
