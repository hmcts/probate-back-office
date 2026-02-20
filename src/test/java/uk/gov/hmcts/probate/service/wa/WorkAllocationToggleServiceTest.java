package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(MockitoJUnitRunner.class)
class WorkAllocationToggleServiceTest {

    private WorkAllocationToggleService workAllocationToggleService = new WorkAllocationToggleService();

    @Test
    void isProbateWAToggleEnabled() {
        ReflectionTestUtils.setField(workAllocationToggleService, "probateWAEnabled", true);
        assertTrue(workAllocationToggleService.isProbateWAEnabledToggleOn());
    }

    @Test
    void isProbateWAToggleDisabled() {
        ReflectionTestUtils.setField(workAllocationToggleService, "probateWAEnabled", false);
        assertFalse(workAllocationToggleService.isProbateWAEnabledToggleOn());
    }
}
