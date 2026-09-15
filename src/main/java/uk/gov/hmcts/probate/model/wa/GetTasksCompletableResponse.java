package uk.gov.hmcts.probate.model.wa;

import java.util.List;

public record GetTasksCompletableResponse<T extends TaskData>(boolean taskRequiredForEvent,
                                                              List<T> tasks) {
}
