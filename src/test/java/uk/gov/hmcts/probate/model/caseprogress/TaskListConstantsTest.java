package uk.gov.hmcts.probate.model.caseprogress;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.StateConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.caseprogress.TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS;

class TaskListConstantsTest {
    @Test
    void testConstantsMapCorrectly() {
        assertAll("Task list state mapping",
                () -> assertEquals(TL_STATE_ADD_SOLICITOR_DETAILS, TaskListState.mapCaseState(null, null, false)),
                () -> assertEquals(TL_STATE_ADD_SOLICITOR_DETAILS,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED_SOLICITOR_DTLS, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED_DECEASED_DTLS, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_PROBATE_CREATED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_INTESTACY_CREATED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_ADMON_CREATED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_REVIEW_AND_SUBMIT,
                        TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_UPDATED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_CREATE_SERVICE_REQUEST,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_AWAITING_PAYMENT, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_CREATE_SERVICE_REQUEST,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "Yes", false)),
                () -> assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "NotApplicable", false)),
                () -> assertEquals(TaskListState.TL_STATE_PAYMENT_ATTEMPTED,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "No", false)),
                () -> assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                        TaskListState.mapCaseState(StateConstants.STATE_CASE_PRINTED, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING_REISSUE, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_EXAMINING, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_READY_TO_ISSUE, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_WORKER_ESCALATION, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_ISSUE_GRANT,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_ISSUE_GRANT, null, false)),
                () -> assertEquals(TaskListState.TL_STATE_COMPLETE,
                        TaskListState.mapCaseState(StateConstants.STATE_BO_GRANT_ISSUED, null, false))
        );
    }
}
