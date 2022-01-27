package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.caseprogress.CaseProgressState;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskListRendererFactory {
    private final StoppedTaskListRenderer stoppedTaskListRenderer;
    private final EscalatedTaskListRenderer escalatedTaskListRenderer;
    private final AppStoppedTaskListRenderer appStoppedTaskListRenderer;
    private final DefaultTaskListRenderer defaultTaskListRenderer;
    
    public BaseTaskListRenderer getTaskListRenderer(String applicationState) {
        if (applicationState == null) {
            return defaultTaskListRenderer;
        }
        CaseProgressState progressState = CaseProgressState.mapCaseState(applicationState);

        switch (progressState) {
            case CASE_STOPPED:
                return stoppedTaskListRenderer;
            case CASE_ESCALATED:
                return escalatedTaskListRenderer;
            case APPLICATION_STOPPED:
                return appStoppedTaskListRenderer;
            default:
                return defaultTaskListRenderer;
        }
    }
}
