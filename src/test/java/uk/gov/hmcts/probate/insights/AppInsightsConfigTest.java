package uk.gov.hmcts.probate.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppInsightsConfigTest {

    @Test
    void shouldCreateAppInsightsClient() {
        final AppInsightsConfig appInsightsConfig = new AppInsightsConfig();
        assertThat(appInsightsConfig.telemetryClient()).isInstanceOf(TelemetryClient.class);
    }
}
