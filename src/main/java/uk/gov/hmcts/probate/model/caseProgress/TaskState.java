package uk.gov.hmcts.probate.model.caseProgress;

import static java.lang.String.format;

public enum TaskState {
    COMPLETED(TaskStateDescriptions.COMPLETED_DESC, TaskStateImageUrls.COMPLETED_URL),
    IN_PROGRESS(TaskStateDescriptions.IN_PROGRESS_DESC, TaskStateImageUrls.IN_PROGRESS_URL),
    NOT_STARTED(TaskStateDescriptions.NOT_STARTED_DESC, TaskStateImageUrls.NOT_STARTED_URL),
    NOT_AVAILABLE(TaskStateDescriptions.NOT_AVAILABLE_DESC, "");

    public final String displayText;
    public final String imageUrl;

    TaskState(String displayText, String imageUrl) {
        this.displayText = displayText;
        this.imageUrl = imageUrl;
    }

    private static class TaskStateDescriptions {
        public static final String  COMPLETED_DESC = "COMPLETED";
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_DESC = "IN PROGRESS";
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_DESC = "NOT STARTED";
        public static final String  NOT_AVAILABLE_DESC = "";
    }

    private static class TaskStateImageUrls {
        private static final String  URL_TEMPLATE = "https://raw.githubusercontent.com/hmcts/probate-back-office/master/src/main/resources/statusImages/%s";
        public static final String  COMPLETED_URL = format(URL_TEMPLATE, "completed.png");
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_URL = format(URL_TEMPLATE, "in-progress.png");
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_URL = format(URL_TEMPLATE, "not-started.png");;
    }
}
