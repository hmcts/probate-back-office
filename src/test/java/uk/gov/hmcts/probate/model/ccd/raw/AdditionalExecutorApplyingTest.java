package uk.gov.hmcts.probate.model.ccd.raw;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class AdditionalExecutorApplyingTest {

    private static final String APPLYING_EXECUTOR_AGREED = "true";
    private static final String APPLYING_EXECUTOR_INVITATION_ID = "1234567890";
    private AdditionalExecutorApplying additionalExecutorApplying;

    @Before
    public void setUp() throws Exception {
        additionalExecutorApplying = AdditionalExecutorApplying.builder().applyingExecutorAgreed(APPLYING_EXECUTOR_AGREED).applyingExecutorInvitationId(APPLYING_EXECUTOR_INVITATION_ID).build();
    }

    @Test
    public void testGetApplyingExecutorAgreed() {
        assertEquals(APPLYING_EXECUTOR_AGREED, additionalExecutorApplying.getApplyingExecutorAgreed());
    }

    @Test
    public void testGetApplyingExecutorInvitationId() {
        assertEquals(APPLYING_EXECUTOR_INVITATION_ID, additionalExecutorApplying.getApplyingExecutorInvitationId());
    }
}
