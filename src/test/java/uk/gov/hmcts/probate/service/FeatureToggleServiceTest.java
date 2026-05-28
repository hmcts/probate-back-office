package uk.gov.hmcts.probate.service;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureToggleServiceTest {
    @Mock
    private LDClient ldClient;

    private FeatureToggleService featureToggleService;

    @BeforeEach
    void setUp() {
        featureToggleService = new FeatureToggleService(ldClient, "probate", "Probate", "Backend");
    }

    @Test
    void shouldUseSmeeAndFordEmailLaunchDarklyFlagWithFalseDefault() {
        when(ldClient.boolVariation(eq("probate-disable-smee-ford-email"), any(LDContext.class), eq(false))).thenReturn(true);

        assertTrue(featureToggleService.isProbateDisableSmeeAndFordEmailFeatureEnabled());

        verify(ldClient).boolVariation(eq("probate-disable-smee-ford-email"), any(LDContext.class), eq(false));
    }
}
