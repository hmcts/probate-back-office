package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.caseprogress.CaseProgressState;

public class TaskListRendererFactory {

    public BaseTaskListRenderer getTaskListRenderer(String applicationState) {
        if (applicationState == null) {
            return new DefaultTaskListRenderer();
        }
        CaseProgressState progressState = CaseProgressState.mapCaseState(applicationState);

        switch (progressState) {
            case CASE_STOPPED:
                return new StoppedTaskListRenderer();
            case CASE_ESCALATED:
                return new EscalatedTaskListRenderer();
            case APPLICATION_STOPPED:
                return new AppStoppedTaskListRenderer();
            default:
                return new DefaultTaskListRenderer();
        }
    }
}
