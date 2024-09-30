package uk.gov.hmcts.probate.model.caseprogress;

import static java.lang.String.format;

public enum TaskState {
    COMPLETED(TaskStateDescriptions.COMPLETED_DESC, TaskStateImageUrls.COMPLETED_URL,
            TaskStateDescriptions.COMPLETED_DESC_WELSH, TaskStateImageUrls.COMPLETED_URL_WELSH),
    IN_PROGRESS(TaskStateDescriptions.IN_PROGRESS_DESC, TaskStateImageUrls.IN_PROGRESS_URL,
            TaskStateDescriptions.IN_PROGRESS_DESC_WELSH, TaskStateImageUrls.IN_PROGRESS_URL_WELSH),
    NOT_STARTED(TaskStateDescriptions.NOT_STARTED_DESC, TaskStateImageUrls.NOT_STARTED_URL,
            TaskStateDescriptions.NOT_STARTED_DESC_WELSH, TaskStateImageUrls.NOT_STARTED_URL_WELSH),
    NOT_AVAILABLE(TaskStateDescriptions.NOT_AVAILABLE_DESC, "",
            TaskStateDescriptions.NOT_AVAILABLE_DESC_WELSH,"");

    public final String displayText;
    public final String imageUrl;
    public final String displayTextWelsh;
    public final String imageUrlWelsh;

    TaskState(String displayText, String imageUrl, String displayTextWelsh, String imageUrlWelsh) {
        this.displayText = displayText;
        this.imageUrl = imageUrl;
        this.displayTextWelsh = displayTextWelsh;
        this.imageUrlWelsh = imageUrlWelsh;
    }

    private static class TaskStateDescriptions {
        public static final String  COMPLETED_DESC = "COMPLETED";
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_DESC = "IN PROGRESS";
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_DESC = "NOT STARTED";
        public static final String  NOT_AVAILABLE_DESC = "";
        public static final String  COMPLETED_DESC_WELSH = "CWBLHAWYD";
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_DESC_WELSH = "YN MYND RHAGDDO";
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_DESC_WELSH = "HEB DDECHRAU";
        public static final String  NOT_AVAILABLE_DESC_WELSH = "";
    }

    public static final String CODE_BRANCH = "master";
    public static final String IMG_URL_TEMPLATE = "https://raw.githubusercontent.com/hmcts/probate-back-office/" + CODE_BRANCH + "/src/main/resources/statusImages/%s";

    public static class TaskStateImageUrls {
        public static final String  COMPLETED_URL = format(IMG_URL_TEMPLATE, "completed.png");
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_URL = format(IMG_URL_TEMPLATE, "in-progress.png");
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_URL = format(IMG_URL_TEMPLATE, "not-started.png");

        public static final String  COMPLETED_URL_WELSH = format(IMG_URL_TEMPLATE, "completed-welsh.png");
        // display for current multi-state tasklist state
        public static final String  IN_PROGRESS_URL_WELSH = format(IMG_URL_TEMPLATE, "in-progress-welsh.png");
        // display for current single-state tasklist state
        public static final String  NOT_STARTED_URL_WELSH = format(IMG_URL_TEMPLATE, "not-started-welsh.png");

        private TaskStateImageUrls() {
            throw new IllegalStateException("Utility class");
        }
    }
}
