package uk.gov.hmcts.probate.model.caseProgress;

public enum TaskState {
    COMPLETED(TaskStateDescriptions.COMPLETED_DESC),
    IN_PROGRESS(TaskStateDescriptions.IN_PROGRESS_DESC),
    NOT_STARTED(TaskStateDescriptions.NOT_STARTED_DESC),
    NOT_AVAILABLE(TaskStateDescriptions.NOT_AVAILABLE_DESC);

    public String displayText;

    TaskState(String displayText) {
        this.displayText = displayText;
    }

    private static class TaskStateDescriptions {
        public static final String  COMPLETED_DESC = "COMPLETED";
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_DESC = "IN PROGRESS";
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_DESC = "NOT STARTED";
        public static final String  NOT_AVAILABLE_DESC = "";

    }
}
