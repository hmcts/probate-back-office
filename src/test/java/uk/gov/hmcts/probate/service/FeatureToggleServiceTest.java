package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class FeatureToggleServiceTest {

    @InjectMocks
    private FeatureToggleService featureToggleService;

    @Test
    void isProbateWAToggleEnabled() {
        ReflectionTestUtils.setField(featureToggleService, "probateWAEnabled", true);
        assertTrue(featureToggleService.isProbateWAEnabledToggleOn());
    }

    @Test
    void isProbateWAToggleDisabled() {
        ReflectionTestUtils.setField(featureToggleService, "probateWAEnabled", false);
        assertFalse(featureToggleService.isProbateWAEnabledToggleOn());
    }
}

