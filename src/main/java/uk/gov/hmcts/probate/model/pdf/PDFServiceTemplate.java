package uk.gov.hmcts.probate.model.pdf;

public enum PDFServiceTemplate {

    LEGAL_STATEMENT("legalStatement");

    private String htmlFileName;

    PDFServiceTemplate(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    public String getHtmlFileName() {
        return htmlFileName;
    }

}
