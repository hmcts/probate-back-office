package uk.gov.hmcts.probate.model.ccd.raw;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionalExecutorNotApplyingTest {

    private static final String EXEC_NAME_ON_WILL = "execNameOnWill";
    private static final String EXEC_NAME = "execName";
    private static final String UNMATCHED_NAME = "unmatchedName";

    private AdditionalExecutorNotApplying.AdditionalExecutorNotApplyingBuilder additionalExecutorNotApplyingBuilder;

    @BeforeEach
    public void setUP() {
        additionalExecutorNotApplyingBuilder = AdditionalExecutorNotApplying
            .builder();
    }

    @Test
    void shouldReturnFalseIfNull() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .build();
        assertFalse(additionalExecutorNotApplying.hasName(null));
    }

    @Test
    void shouldReturnFalseIfNoMatch() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorName(EXEC_NAME)
                .notApplyingExecutorNameOnWill(EXEC_NAME_ON_WILL)
                .build();
        assertFalse(additionalExecutorNotApplying.hasName(UNMATCHED_NAME));
    }

    @Test
    void shouldReturnTrueIfNameMatches() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorName(EXEC_NAME)
                .build();
        assertTrue(additionalExecutorNotApplying.hasName(EXEC_NAME));
    }

    @Test
    void shouldReturnTrueIfNameOnWillMatches() {
        final AdditionalExecutorNotApplying additionalExecutorNotApplying =
            additionalExecutorNotApplyingBuilder
                .notApplyingExecutorNameOnWill(EXEC_NAME_ON_WILL)
                .build();
        assertTrue(additionalExecutorNotApplying.hasName(EXEC_NAME_ON_WILL));
    }
}
