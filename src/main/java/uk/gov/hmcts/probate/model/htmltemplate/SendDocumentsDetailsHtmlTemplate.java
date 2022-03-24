package uk.gov.hmcts.probate.model.htmltemplate;

import uk.gov.hmcts.probate.model.PageTextConstants;

import static java.lang.String.format;

public class SendDocumentsDetailsHtmlTemplate {
    public static final String DOC_DETAILS =
            format("%s<br/><ul><li>%s</li><li>%s</li>%s%s%s%s%s%s%s%s%s%s</ul>",
                    PageTextConstants.DOCUMENT_NOW_SEND_US,
                    PageTextConstants.DOCUMENT_YOUR_REF_NUM,
                    PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY,
                    PageTextConstants.ORIGINAL_WILL,
                    PageTextConstants.IHT_TEXT,
                    PageTextConstants.IHT_FORM,
                    PageTextConstants.IHT_ESTATE_207,
                    PageTextConstants.PA14_FORM,
                    PageTextConstants.PA15_FORM,
                    PageTextConstants.PA16_FORM,
                    PageTextConstants.PA17_FORM,
                    PageTextConstants.ADMON_WILL_RENUNCIATION,
                    PageTextConstants.TC_RESOLUTION_WITH_APP);

    private SendDocumentsDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
