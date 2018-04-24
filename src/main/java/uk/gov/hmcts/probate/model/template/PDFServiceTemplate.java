package uk.gov.hmcts.probate.model.template;

public enum PDFServiceTemplate {

    LEGAL_STATEMENT("legalStatement");

    private final String htmlFileName;

    PDFServiceTemplate(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    public String getHtmlFileName() {
        return htmlFileName;
    }
}
