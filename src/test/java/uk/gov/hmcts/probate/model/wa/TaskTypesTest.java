package uk.gov.hmcts.probate.model.wa;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.StateConstants;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReasonId;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.probate.model.StateConstants.STATE_CASE_PRINTED;

class TaskTypesTest {

    @Test
    void examineAwaitingDocTasks() {
        TaskTypes.examineAwaitingDocTasks()
                .forEach(taskType ->
                        assertThat(StateConstants.STATE_CASE_PRINTED)
                                .isEqualTo(taskType.getState()));
    }

    @Test
    void getStateTasks() {
        Map<String, TaskTypes> stateTasks = TaskTypes.getStateTasks(StateConstants.STATE_CASE_PRINTED);
        String key = ("Examine" + HandoffReasonId.DOUBLE_PROBATE.getCode() + STATE_CASE_PRINTED).toUpperCase();
        assertThat(stateTasks.get(key).closingEventId.getName())
                .isEqualTo(TaskTypes.EXAMINE_DOUBLE_PROBATE.closingEventId.getName());
        assertThat(stateTasks.get(key).getClosingEventId().getName())
                .isEqualTo(TaskTypes.EXAMINE_DOUBLE_PROBATE.getClosingEventId().getName());
    }

    @Test
    void examineReadyToIssueTasks() {
        TaskTypes.examineReadyToIssueTasks()
                .forEach(taskType ->
                        assertThat(StateConstants.STATE_BO_READY_TO_ISSUE)
                                .isEqualTo(taskType.getState()));
    }

    @Test
    void getOptionalClosingEventId() {
        Optional<EventId> optionalClosingEventId = TaskTypes.EXAMINE_TRUST_CORPORATION.getOptionalClosingEventId();
        assertThat(optionalClosingEventId)
                .contains(TaskTypes.EXAMINE_TRUST_CORPORATION.closingEventId);
    }
}