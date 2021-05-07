package uk.gov.hmcts.probate.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Component
public class AppInsights implements EventRepository {

    private final TelemetryClient telemetry;

    @Autowired
    public AppInsights(@Value("${azure.application-insights.instrumentation-key}")
                                          String instrumentationKey,
                                      TelemetryClient telemetry) {
        TelemetryConfiguration.getActive().setInstrumentationKey(instrumentationKey);
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());
        this.telemetry = telemetry;
    }

    @Override
    public void trackEvent(String name, Map<String, String> properties) {
        telemetry.trackEvent(name, properties,null);
    }

    private Map<String, String> trackingMap(String propertyToTrack) {
        HashMap<String, String> trackMap = new HashMap<String ,String>();
        trackMap.put("url", propertyToTrack);
        return trackMap;
    }
}