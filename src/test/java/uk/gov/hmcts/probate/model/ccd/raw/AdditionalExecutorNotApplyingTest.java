package uk.gov.hmcts.probate.model.ccd.raw;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class AdditionalExecutorNotApplyingTest {

    private static final String EXEC_NAME_ON_WILL = "execNameOnWill";
    private static final String EXEC_NAME = "execName";
    private static final String UNMATCHED_NAME = "unmatchedName";

    private AdditionalExecutorNotApplying.AdditionalExecutorNotApplyingBuilder additionalExecutorNotApplyingBuilder;

    @Before
    public void setUP() {
        additionalExecutorNotApplyingBuilder = AdditionalExecutorNotApplying
            .builder();
    }

    @Test
    public void shouldReturnFalseIfNull() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .build();
        assertFalse(additionalExecutorNotApplying.hasName(null));
    }

    @Test
    public void shouldReturnFalseIfNoMatch() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorNameOnWill(EXEC_NAME_ON_WILL)
                .build();
        assertFalse(additionalExecutorNotApplying.hasName(UNMATCHED_NAME));
    }

    @Test
    public void shouldReturnTrueIfNameMatches() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorName(EXEC_NAME)
                .build();
        assertTrue(additionalExecutorNotApplying.hasName(EXEC_NAME));
    }

    @Test
    public void shouldReturnTrueIfNameOnWillMatches() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorNameOnWill(EXEC_NAME_ON_WILL)
                .build();
        assertTrue(additionalExecutorNotApplying.hasName(EXEC_NAME_ON_WILL));
    }
}
