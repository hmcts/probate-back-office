package uk.gov.hmcts.probate.model.template;

public enum MarkdownTemplate {

    NEXT_STEPS("nextSteps"),
    NEXT_STEPS_NO_DOCUMENTS_REQUIRED("nextStepsNoDocumentsRequired"),
    STOP_BODY("stopBody"),
    CAVEAT_NEXT_STEPS("caveatNextSteps");

    private final String filename;

    MarkdownTemplate(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
