package uk.gov.hmcts.probate.health;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

public class ProbateSolCcdHealthIndicatorTest {

    private final static String VALID_COMMIT_ID = "2921eb6292eab0bd9ce8a42b44cf32fe9f4b0069";
    private final static String EMPTY_COMMIT_ID = "";
    private ProbateSolCcdHealthIndicator probateSolCcdHealthIndicator;

    @Test
    public void shouldDisplayCommitIdWhenNotNull() {

        Status status = StringUtils.isBlank(VALID_COMMIT_ID) ? Status.UNKNOWN : Status.UP;
        probateSolCcdHealthIndicator = new ProbateSolCcdHealthIndicator(VALID_COMMIT_ID);
        Health health = probateSolCcdHealthIndicator.health();

        assertThat(health.getStatus(), is(status.UP));
        assertThat(health.getDetails().get("gitCommitId"), is(VALID_COMMIT_ID));

    }

    @Test
    public void shouldDisplayBlankCommitIdWhenNull() {

        Status status = StringUtils.isBlank(EMPTY_COMMIT_ID) ? Status.UNKNOWN : Status.UP;
        probateSolCcdHealthIndicator = new ProbateSolCcdHealthIndicator(EMPTY_COMMIT_ID);
        Health health = probateSolCcdHealthIndicator.health();

        assertThat(health.getStatus(), is(status.UNKNOWN));
        assertThat(health.getDetails().get("gitCommitId"), is(EMPTY_COMMIT_ID));
    }

}
