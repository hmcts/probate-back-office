package uk.gov.hmcts.probate.model;

public class PageTextConstants {
    public static final String DOCUMENT_NOW_SEND_US = "You now need to send us";
    public static final String DOCUMENT_YOUR_REF_NUM = "your reference number <refNum/> written on a piece of paper";
    public static final String DOCUMENT_IHT_421 = "the stamped (receipted) IHT 421 with this application";
    public static final String DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY = "a photocopy of the signed legal statement and declaration";

    private PageTextConstants() {
        throw new IllegalStateException("Utility class");
    }
}
