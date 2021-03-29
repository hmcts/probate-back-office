package uk.gov.hmcts.probate.model.htmltemplate;

import uk.gov.hmcts.probate.model.PageTextConstants;

import static java.lang.String.format;

public class SendDocumentsDetailsHtmlTemplate {
    public static final String DOC_DETAILS =
            format("%s<br/><ul><li>%s</li><li>%s</li></ul>",
                    PageTextConstants.DOCUMENT_NOW_SEND_US,
                    PageTextConstants.DOCUMENT_YOUR_REF_NUM,
                    PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY);

    private SendDocumentsDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
