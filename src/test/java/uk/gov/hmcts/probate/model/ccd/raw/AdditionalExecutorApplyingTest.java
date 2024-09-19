package uk.gov.hmcts.probate.model.ccd.raw;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdditionalExecutorApplyingTest {

    private static final String APPLYING_EXECUTOR_AGREED = "true";
    private static final String APPLYING_EXECUTOR_INVITATION_ID = "1234567890";
    private AdditionalExecutorApplying additionalExecutorApplying;

    @BeforeEach
    public void setUp() throws Exception {
        additionalExecutorApplying = AdditionalExecutorApplying.builder()
            .applyingExecutorAgreed(APPLYING_EXECUTOR_AGREED)
            .applyingExecutorInvitationId(APPLYING_EXECUTOR_INVITATION_ID).build();
    }

    @Test
    void testGetApplyingExecutorAgreed() {
        assertEquals(APPLYING_EXECUTOR_AGREED, additionalExecutorApplying.getApplyingExecutorAgreed());
    }

    @Test
    void testGetApplyingExecutorInvitationId() {
        assertEquals(APPLYING_EXECUTOR_INVITATION_ID, additionalExecutorApplying.getApplyingExecutorInvitationId());
    }
}
