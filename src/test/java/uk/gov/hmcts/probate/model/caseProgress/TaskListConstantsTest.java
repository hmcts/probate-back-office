package uk.gov.hmcts.probate.model.caseProgress;
import org.junit.Test;
import uk.gov.hmcts.probate.model.StateConstants;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.caseProgress.TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS;

public class TaskListConstantsTest {
    @Test
    public void testConstantsMapCorrectly() {
        assertEquals(TaskListState.mapCaseState(null), TL_STATE_ADD_SOLICITOR_DETAILS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_CREATED), TaskListState.TL_STATE_ADD_DECEASED_DETAILS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_SOL_PROBATE_CREATED), TaskListState.TL_STATE_ADD_APPLICATION_DETAILS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_SOL_INTESTACY_CREATED), TaskListState.TL_STATE_ADD_APPLICATION_DETAILS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_SOL_ADMON_CREATED), TaskListState.TL_STATE_ADD_APPLICATION_DETAILS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_REDEC_NOTIFICATION_SENT), TaskListState.TL_STATE_SEND_DOCUMENTS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_SOL_APP_UPDATED), TaskListState.TL_STATE_REVIEW_AND_SUBMIT);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_CASE_CREATED), TaskListState.TL_STATE_SEND_DOCUMENTS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_CASE_PRINTED), TaskListState.TL_STATE_SEND_DOCUMENTS);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_READY_FOR_EXAMINATION), TaskListState.TL_STATE_EXAMINE_APPLICATION);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING), TaskListState.TL_STATE_EXAMINE_APPLICATION);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_EXAMINING_REISSUE), TaskListState.TL_STATE_EXAMINE_APPLICATION);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_EXAMINING), TaskListState.TL_STATE_EXAMINE_APPLICATION);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_READY_TO_ISSUE), TaskListState.TL_STATE_EXAMINE_APPLICATION);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_CASE_MATCHING_ISSUE_GRANT), TaskListState.TL_STATE_ISSUE_GRANT);
        assertEquals(TaskListState.mapCaseState(StateConstants.STATE_BO_GRANT_ISSUED), TaskListState.TL_STATE_COMPLETE);
    }
}
