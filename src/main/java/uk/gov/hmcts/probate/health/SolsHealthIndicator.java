package uk.gov.hmcts.probate.health;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
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

    private static final String EXCEPTION_KEY = "exception";
    private static final String MESSAGE_KEY = "message";
    private static final String URL_KEY = "url";
    private static final String GIT_COMMIT_ID_KEY = "gitCommitId";

    private final String url;
    private final RestTemplate restTemplate;

    @Value("${git.commit.id}")
    private static String commitId;

    @Override
    public Health health() {
        ResponseEntity<String> responseEntity;

        if (commitId == null){ commitId = "commit ID is null";}

        try {
            responseEntity = restTemplate.getForEntity(url + "/health", String.class);

        } catch (ResourceAccessException rae) {
            return getHealthWithDownStatus(url, commitId, rae.getMessage(), "ResourceAccessException");
        } catch (HttpStatusCodeException hsce) {
            return getHealthWithDownStatus(url, commitId, hsce.getMessage(),
                    "HttpStatusCodeException - HTTP Status: " + hsce.getStatusCode().value());
        } catch (UnknownHttpStatusCodeException uhsce) {
            return getHealthWithDownStatus(url, commitId, uhsce.getMessage(), "UnknownHttpStatusCodeException - " + uhsce.getStatusText());
        }

        if (responseEntity != null && !responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return getHealthWithDownStatus(url, commitId, "HTTP Status code not 200", "HTTP Status: " + responseEntity.getStatusCodeValue());
        }

        return getHealthWithUpStatus(url, commitId, "HTTP Status OK", "status is okay");

    }

    public static Health getHealthWithUpStatus(String url, String commitId, String message, String status) {
        return Health.up()
                .withDetail(URL_KEY, url)
                .withDetail(GIT_COMMIT_ID_KEY, commitId)
                .withDetail(MESSAGE_KEY, "HTTP Status OK")
                .build();
    }

    public static Health getHealthWithDownStatus(String url, String commitId, String message, String status) {

        return Health.down()
                .withDetail(URL_KEY, url)
                .withDetail(GIT_COMMIT_ID_KEY, commitId)
                .withDetail(MESSAGE_KEY, message)
                .withDetail(EXCEPTION_KEY, status)
                .build();
    }

    public static Health getGitCommit(String GIT_COMMIT_ID_KEY,  String commitId) {
        if (commitId != null){
            return Health.up().withDetail(GIT_COMMIT_ID_KEY, commitId).build();
        }else {
            commitId = "commit id is null";
            return Health.down().withDetail(GIT_COMMIT_ID_KEY, commitId).build();
        }
    }
}
