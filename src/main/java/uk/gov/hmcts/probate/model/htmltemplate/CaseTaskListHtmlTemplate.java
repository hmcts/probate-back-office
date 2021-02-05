package uk.gov.hmcts.probate.model.htmltemplate;

public class CaseTaskListHtmlTemplate {
    private static final String SEPARATOR = "<gridRowSeparator/>\n";
    private static final String CLOSE_GRID_ROW = "</gridCol-one-third></gridRow>\n";
    private static final String CLOSE_GRID_ROW_WITH_EMPTY_COLUMN = "</gridCol-two-thirds><gridCol-one-third>&nbsp;" + CLOSE_GRID_ROW;

    public static final String TASK_LIST_TEMPLATE =
        "<gridRow><gridCol-two-thirds>\n"
        + "<h>1. Enter application details</h>\n"
        + "<gridRow><gridCol-two-thirds><p><secText>These steps are to be completed by the legal professional.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><addSolicitorLink/></p></gridCol-two-thirds><gridCol-one-third><status-addSolicitor/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><addDeceasedLink/></p></gridCol-two-thirds><gridCol-one-third><status-addDeceasedDetails/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><addAppLink/></p></gridCol-two-thirds><gridCol-one-third><status-addApplicationDetails/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<br/>\n"
        + "<h>2. Sign legal statement and submit application</h>\n"
        + "<gridRow><gridCol-two-thirds><p><secText>These steps are to be completed by the legal professional.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><rvwLink/></p></gridCol-two-thirds><gridCol-one-third><status-reviewAndSubmit/>"
        + CLOSE_GRID_ROW
        + "<reviewAndSubmitDate/>"
        + "<gridRow><gridCol-two-thirds><p><secText>The legal statement is generated. You can review, change any details, "
        + "then sign and submit your application.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third>"
        + "</gridRow>\n"
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p>Send documents<br/><sendDocsLink/></p></gridCol-two-thirds><gridCol-one-third>"
        + "<status-sendDocuments/>"
            + CLOSE_GRID_ROW
        + SEPARATOR
        + "<br/>\n"
        + "<h>3. Review application</h>\n"
        + "<gridRow><gridCol-two-thirds><p><secText>These steps are completed by HM Courts and Tribunals Service staff. "
        + "It can take a few weeks before the review starts.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><authDocsLink/></p></gridCol-two-thirds><gridCol-one-third>"
        + "<status-authDocuments/>"
        + CLOSE_GRID_ROW
        + "<authenticatedDate/>"
        + "<gridRow><gridCol-two-thirds><p><secText>We will authenticate your documents and "
        + "match them with your application.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><examAppLink/></p></gridCol-two-thirds><gridCol-one-third><status-examineApp/>"
        + CLOSE_GRID_ROW
        + "<gridRow><gridCol-two-thirds><p><secText>We review your application for incomplete information or problems and validate it "
        + "against other cases or caveats. After the review we prepare the grant.</secText></p></gridCol-two-thirds>"
        + "<gridCol-one-third>&nbsp;"
        + CLOSE_GRID_ROW
        + "<gridRow><gridCol-two-thirds><p><secText>Your application will update through any of these case states as it is "
        + "reviewed by our team:</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + "<ul>\n<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>"
        + SEPARATOR
        + "<h>4. Grant of representation</h>\n"
        + "<gridRow><gridCol-two-thirds><p><secText>This step is completed by HM Courts and Tribunals Service staff.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "<gridRow><gridCol-two-thirds><p><issueGrantLink/>/p></gridCol-two-thirds><gridCol-one-third><status-issueGrant/>"
        + CLOSE_GRID_ROW
        + "<gridRow><gridCol-two-thirds><p><secText>The grant will be delivered in the post a few days after issuing.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "</gridCol-two-thirds>\n"
        + "</gridRow>\n";

    private CaseTaskListHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
