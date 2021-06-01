package uk.gov.hmcts.probate.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

public class AppInsightsTest {
    private AppInsights classUnderTest;
    private String instrumentKey = "key";

    @Mock
    private TelemetryClient telemetryClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TelemetryContext telemetryContext = new TelemetryContext();
        telemetryContext.setInstrumentationKey("some-key");
        doReturn(telemetryContext).when(telemetryClient).getContext();
        classUnderTest = new AppInsights(instrumentKey, telemetryClient);
    }

    @Test
    public void trackRequest() {
        classUnderTest.trackEvent(REQUEST_SENT.toString(), trackingMap("uri", "http://testurl.com"));
    }

    @Test
    public void testTelemetry() {
        TelemetryContext telemetryContext = new TelemetryContext();
        telemetryContext.setInstrumentationKey("key");

        TelemetryClient telemetryClient = mock(TelemetryClient.class);
        when(telemetryClient.getContext()).thenReturn(telemetryContext);

        AppInsights appInsights = new AppInsights(instrumentKey, telemetryClient);

        Assert.isInstanceOf(AppInsights.class, appInsights);
    }

    private Map<String, String> trackingMap(String propertyname, String propertyToTrack) {
        HashMap<String, String> trackMap = new HashMap<String, String>();
        trackMap.put(propertyname, propertyToTrack);
        return trackMap;
    }

}