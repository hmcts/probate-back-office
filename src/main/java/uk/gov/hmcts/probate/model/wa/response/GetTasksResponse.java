package uk.gov.hmcts.probate.model.wa.response;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.gov.hmcts.probate.model.wa.TaskData;

import java.util.List;

@EqualsAndHashCode
@ToString
public class GetTasksResponse<T extends TaskData> {

    private final List<T> tasks;

    private final long totalRecords;

    public GetTasksResponse(List<T> tasks, long totalRecords) {
        this.tasks = tasks;
        this.totalRecords = totalRecords;
    }

    public List<T> getTasks() {
        return tasks;
    }

    public long getTotalRecords() {
        return totalRecords;
    }
}
