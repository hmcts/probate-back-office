package uk.gov.hmcts.probate.health;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

@AllArgsConstructor
public class ProbateSolCcdHealthIndicator implements HealthIndicator {

    private final String commitId;

    private static final String GIT_COMMIT_ID_KEY = "gitCommitId";
    private static final String GIT_COMMIT_ID_UNKNOWN_VALUE = "";

    @Override
    public Health health() {
        Status status = StringUtils.isBlank(commitId) ? Status.UNKNOWN : Status.UP;
        return Health.status(status)
                .withDetail(GIT_COMMIT_ID_KEY, StringUtils.defaultString(commitId, GIT_COMMIT_ID_UNKNOWN_VALUE))
                .build();
    }
}
