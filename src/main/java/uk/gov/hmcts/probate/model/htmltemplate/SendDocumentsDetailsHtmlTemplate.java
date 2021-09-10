package uk.gov.hmcts.probate.model.htmltemplate;

import uk.gov.hmcts.probate.model.PageTextConstants;

import static java.lang.String.format;

public class SendDocumentsDetailsHtmlTemplate {
    public static final String DOC_DETAILS =
            format("%s<br/><ul><li>%s</li><li>%s</li>%s%s%s%s</ul>",
                    PageTextConstants.DOCUMENT_NOW_SEND_US,
                    PageTextConstants.DOCUMENT_YOUR_REF_NUM,
                    PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY,
                    PageTextConstants.ORIGINAL_WILL,
                    PageTextConstants.IHT_TEXT,
                    PageTextConstants.IHT_FORM,
                    PageTextConstants.RENOUNCING_EXECUTORS);

    private SendDocumentsDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
