package uk.gov.hmcts.probate.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import static org.mockito.Mockito.*;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

public class AppInsightsTest {
    private AppInsights classUnderTest;

    @Mock
    private TelemetryClient telemetryClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TelemetryContext telemetryContext = new TelemetryContext();
        telemetryContext.setInstrumentationKey("some-key");
        doReturn(telemetryContext).when(telemetryClient).getContext();
        classUnderTest = new AppInsights(telemetryClient);
    }

    @Test
    public void trackRequest() {
        classUnderTest.trackEvent(REQUEST_SENT, "uri");

    }

    @Test
    public void testTelemetry() {
        TelemetryContext telemetryContext = new TelemetryContext();
        telemetryContext.setInstrumentationKey("key");

        TelemetryClient telemetryClient = mock(TelemetryClient.class);
        when(telemetryClient.getContext()).thenReturn(telemetryContext);

        AppInsights appInsights = new AppInsights(telemetryClient);

        Assert.isInstanceOf(AppInsights.class, appInsights);
    }

}