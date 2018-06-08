package uk.gov.hmcts.probate.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;

import static java.util.Collections.singletonMap;

@Component
public class AppInsights extends AbstractAppInsights {

    @Autowired
    public AppInsights(TelemetryClient client) {
        super(client);
    }

    public void trackEvent(AppInsightsEvent appInsightsEvent, String caseId) {
        telemetry.trackEvent(appInsightsEvent.toString(), singletonMap("CaseID", caseId), null);
    }
}