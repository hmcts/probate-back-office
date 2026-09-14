package uk.gov.hmcts.probate.model.wa.response;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.model.wa.TaskData;

import java.util.List;

@EqualsAndHashCode
@ToString
public class GetTasksCompletableResponse<T extends TaskData> {

    private final boolean taskRequiredForEvent;
    private final List<T> tasks;

    public GetTasksCompletableResponse(boolean taskRequiredForEvent, List<T> tasks) {
        this.taskRequiredForEvent = taskRequiredForEvent;
        this.tasks = tasks;
    }

    public boolean isTaskRequiredForEvent() {
        return taskRequiredForEvent;
    }

    public List<T> getTasks() {
        return tasks;
    }
}
