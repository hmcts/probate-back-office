package uk.gov.hmcts.probate.model;

public class PageTextConstants {
    public static final String DOCUMENT_NOW_SEND_US = "You now need to send us";
    public static final String DOCUMENT_YOUR_REF_NUM =
        "the printed coversheet (accessed in the cover sheet tab) or your reference number <refNum/> written"
            + " on a sheet of paper";
    public static final String DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY =
        "a photocopy of the signed legal statement and declaration";
    public static final String ORIGINAL_WILL = "<originalWill/>";
    public static final String IHT_TEXT = "<ihtText/>";
    public static final String IHT_FORM = "<ihtForm/>";
    public static final String RENOUNCING_EXECUTORS = "<renouncingExecutors/>";

    private PageTextConstants() {
        throw new IllegalStateException("Utility class");
    }
}
