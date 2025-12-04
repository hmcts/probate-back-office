package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class FeatureToggleServiceTest {

    @InjectMocks
    private FeatureToggleService featureToggleService;

    @Test
    void isProbateWAToggleEnabled() {
        assertTrue(featureToggleService.isProbateWAEnabledToggleOn());
    }
}

