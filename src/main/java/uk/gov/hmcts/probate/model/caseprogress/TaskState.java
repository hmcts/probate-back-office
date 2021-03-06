package uk.gov.hmcts.probate.model.caseprogress;

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

    public static final String CODE_BRANCH = "master";
    public static final String IMG_URL_TEMPLATE = "https://raw.githubusercontent.com/hmcts/probate-back-office/" + CODE_BRANCH + "/src/main/resources/statusImages/%s";

    public static class TaskStateImageUrls {
        public static final String  COMPLETED_URL = format(IMG_URL_TEMPLATE, "completed.png");
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_URL = format(IMG_URL_TEMPLATE, "in-progress.png");
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_URL = format(IMG_URL_TEMPLATE, "not-started.png");

        private TaskStateImageUrls() {
            throw new IllegalStateException("Utility class");
        }
    }
}
