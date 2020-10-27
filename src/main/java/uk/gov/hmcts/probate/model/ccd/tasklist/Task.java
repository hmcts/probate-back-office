package uk.gov.hmcts.probate.model.ccd.tasklist;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationState;

import java.util.Optional;

@Data
@Builder
public class Task {
    private final ApplicationState state;
    private final TaskState taskState;
    private String hint;

    public Task withHint(String hint) {
        this.setHint(hint);
        return this;
    }

    public Optional<String> getHint() {
        return Optional.ofNullable(hint);
    }

    public static Task task(ApplicationState state, TaskState taskState) {
        return Task.builder()
                .state(state)
                .taskState(taskState)
                .build();
    }
}
