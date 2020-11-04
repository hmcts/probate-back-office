package uk.gov.hmcts.probate.model.htmlTemplate;

import uk.gov.hmcts.probate.model.PageTextConstants;

import static java.lang.String.format;

public class SendDocumentsDetailsHtmlTemplate {
    public static final String docDetails =
            format("%s<br/><ul><li>%s</li><li>%s</li><li>%s</li></ul>",
                    PageTextConstants.documentNowSendUs,
                    PageTextConstants.documentYourRefNum,
                    PageTextConstants.documentIht421,
                    PageTextConstants.documentLegalStatementPhotocopy);
}
