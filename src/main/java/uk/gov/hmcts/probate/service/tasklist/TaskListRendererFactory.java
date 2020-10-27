package uk.gov.hmcts.probate.service.tasklist;

public class TaskListRendererFactory {

    public static BaseTaskListRenderer getTaskListRenderer(String applicationState) {
        final String stopped = "BoCaseStopped";
        final String escalated = "BORegistrarEscalation";

        switch (applicationState) {
            case stopped:
                return new StoppedTaskListRenderer();
            case escalated:
                return new EscalatedTaskListRenderer();
            default:
                return new DefaultTaskListRenderer();
        }
    }
}
