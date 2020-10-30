package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.caseProgress.TaskListState;
import uk.gov.hmcts.probate.model.caseProgress.TaskState;

import static java.lang.String.format;

public class TaskStateStatusRenderer {
    public static String renderByReplace(TaskListState currState, String html) {
        return html == null ? null : html
                .replaceFirst("<status-addSolicitor>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_ADD_SOLICITOR_DETAILS)))
                .replaceFirst("<status-addDeceasedDetails>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_ADD_DECEASED_DETAILS)))
                .replaceFirst("<status-addApplicationDetails>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_ADD_APPLICATION_DETAILS)))
                .replaceFirst("<status-reviewAndSubmit>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_REVIEW_AND_SUBMIT)))
                .replaceFirst("<status-sendDocuments>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_SEND_DOCUMENTS)))
                .replaceFirst("<status-authDocuments>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_AUTHENTICATE_DOCUMENTS)))
                .replaceFirst("<status-examineApp>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_EXAMINE_APPLICATION)))
                .replaceFirst("<status-issueGrant>", renderTaskStateTag(GetTaskState(currState, TaskListState.TL_STATE_ISSUE_GRANT)));

    }

    private static TaskState GetTaskState  (TaskListState currState, TaskListState renderState) {
        if (currState == renderState) {
            return currState.isMultiState ? TaskState.IN_PROGRESS : TaskState.NOT_STARTED;
        }
        if (currState.compareTo(renderState) > 0) {
            return TaskState.COMPLETED;
        }
        return TaskState.NOT_AVAILABLE;
    }

    private static String renderTaskStateTag (TaskState taskState) {
        return format("<strong class=\"govuk-tag %1$s\">%2$s</strong>", renderTagColourClass(taskState), taskState.displayText);
    }

    private static String renderTagColourClass(TaskState taskState)  {
        if (taskState == TaskState.NOT_STARTED) {
            return "govuk-tag--grey";
        }
        if (taskState == TaskState.IN_PROGRESS) {
            return "govuk-tag--blue";
        }
        return ""; // completed
    }
}
