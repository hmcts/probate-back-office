package uk.gov.hmcts.probate.model.caseprogress;
import org.junit.Test;
import uk.gov.hmcts.probate.model.StateConstants;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.caseprogress.TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS;

public class TaskListConstantsTest {
    @Test
    public void testConstantsMapCorrectly() {
        assertEquals(TL_STATE_ADD_SOLICITOR_DETAILS, TaskListState.mapCaseState(null));
        assertEquals(TaskListState.TL_STATE_ADD_DECEASED_DETAILS, TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS, TaskListState.mapCaseState(StateConstants.STATE_SOL_PROBATE_CREATED));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS, TaskListState.mapCaseState(StateConstants.STATE_SOL_INTESTACY_CREATED));
        assertEquals(TaskListState.TL_STATE_ADD_APPLICATION_DETAILS, TaskListState.mapCaseState(StateConstants.STATE_SOL_ADMON_CREATED));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS, TaskListState.mapCaseState(StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT));
        assertEquals(TaskListState.TL_STATE_REVIEW_AND_SUBMIT, TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_UPDATED));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS, TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED));
        assertEquals(TaskListState.TL_STATE_SEND_DOCUMENTS, TaskListState.mapCaseState(StateConstants.STATE_CASE_PRINTED));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION, TaskListState.mapCaseState(StateConstants.STATE_BO_READY_FOR_EXAMINATION));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION, TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION, TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING_REISSUE));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION, TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_EXAMINING));
        assertEquals(TaskListState.TL_STATE_EXAMINE_APPLICATION, TaskListState.mapCaseState(StateConstants.STATE_BO_READY_TO_ISSUE));
        assertEquals(TaskListState.TL_STATE_ISSUE_GRANT, TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_ISSUE_GRANT));
        assertEquals(TaskListState.TL_STATE_COMPLETE, TaskListState.mapCaseState(StateConstants.STATE_BO_GRANT_ISSUED));
    }
}
