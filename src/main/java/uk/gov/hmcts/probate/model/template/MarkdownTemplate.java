package uk.gov.hmcts.probate.model.template;

public enum MarkdownTemplate {

    NEXT_STEPS("nextSteps"),
    STOP_BODY("stopBody");

    private final String filename;

    MarkdownTemplate(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
