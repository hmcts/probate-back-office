package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.CaseProgressState;

public class TaskListRendererFactory {

    public static BaseTaskListRenderer getTaskListRenderer(String applicationState) {
        if (applicationState == null) {
            return new DefaultTaskListRenderer();
        }
        CaseProgressState progressState = CaseProgressState.MapCaseState(applicationState);

        switch (progressState) {
            case CASE_STOPPED:
                return new StoppedTaskListRenderer();
            case CASE_ESCALATED:
                return new EscalatedTaskListRenderer();
            default:
                return new DefaultTaskListRenderer();
        }
    }
}
