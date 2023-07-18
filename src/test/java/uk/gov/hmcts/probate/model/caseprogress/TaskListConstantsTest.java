package uk.gov.hmcts.probate.model.caseprogress;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.StateConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.caseprogress.TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS;

class TaskListConstantsTest {
    @Test
    void testConstantsMapCorrectly() {
        assertEquals(TL_STATE_ADD_SOLICITOR_DETAILS, TaskListState.mapCaseState(null, null));
        assertEquals(TL_STATE_ADD_SOLICITOR_DETAILS,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED_SOLICITOR_DTLS, null));
        assertEquals(TaskListState.TL_STATE_ADD_DECEASED_DETAILS,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED_DECEASED_DTLS, null));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_PROBATE_CREATED, null));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_INTESTACY_CREATED, null));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_ADMON_CREATED, null));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                TaskListState.mapCaseState(StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT, null));
        assertEquals(TaskListState.TL_STATE_REVIEW_AND_SUBMIT,
                TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_UPDATED, null));
        assertEquals(TaskListState.TL_STATE_MAKE_PAYMENT,
                TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, null));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "Yes"));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "NotApplicable"));
        assertEquals(TaskListState.TL_STATE_PAYMENT_ATTEMPTED,
                TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED, "No"));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS,
                TaskListState.mapCaseState(StateConstants.STATE_CASE_PRINTED, null));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING_REISSUE, null));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_EXAMINING, null));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION,
                TaskListState.mapCaseState(StateConstants.STATE_BO_READY_TO_ISSUE, null));
        assertEquals(TaskListState.TL_STATE_ISSUE_GRANT,
                TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_ISSUE_GRANT, null));
        assertEquals(TaskListState.TL_STATE_COMPLETE,
                TaskListState.mapCaseState(StateConstants.STATE_BO_GRANT_ISSUED, null));
    }
}
