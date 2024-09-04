package uk.gov.hmcts.probate.model.htmltemplate;

public class CaseTaskListHtmlTemplate {
    private static final String SEPARATOR = "<gridRowSeparator/>\n";
    private static final String OPEN_ROW = "<gridRow><gridCol-two-thirds>";
    private static final String CLOSE_COL1_OPEN_COL2 = "</gridCol-two-thirds><gridCol-one-third>";
    private static final String CLOSE_GRID_ROW = "</gridCol-one-third></gridRow>\n";
    private static final String CLOSE_GRID_ROW_WITH_EMPTY_COLUMN = CLOSE_COL1_OPEN_COL2 + "&nbsp;"
            + CLOSE_GRID_ROW;

    public static final String TASK_LIST_TEMPLATE =
        OPEN_ROW
        + "\n"
        + "<h>1. Enter application details</h>\n"
        + "<h>1. Rhoi manylion y cais</h>\n"
        + "<gridRow><gridCol-two-thirds><p><secText>These steps are to be completed by "
        + "the Probate practitioner.</secText></p>"
        + "<p><secText>Dylai'r camau hyn gael eu cwblhau gan yr ymarferydd profiant.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><addSolicitorLink/></p>"
        + "<p><addSolicitorLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-addSolicitor/>"
        + "<status-addSolicitorWelsh/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + OPEN_ROW
        + "<p><addDeceasedLink/></p>"
        + "<p><addDeceasedLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-addDeceasedDetails/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + OPEN_ROW
        + "<p><addAppLink/></p>"
        + "<p><addAppLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-addApplicationDetails/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<br/>\n"
        + "<h>2. Sign legal statement and submit application</h>\n"
        + "<h>2. Llofnodi'r datganiad cyfreithiol a chyflwyno'r cais</h>\n"
        + OPEN_ROW
        + "<p><secText>These steps are to be completed by the "
        + "Probate practitioner.</secText></p>"
        + "<p><secText>Dylai'r camau hyn gael eu cwblhau gan yr ymarferydd profiant.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><rvwLink/></p>"
        + "<p><rvwLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-reviewAndSubmit/>"
        + CLOSE_GRID_ROW
        + "<reviewAndSubmitDate/>"
        + OPEN_ROW
        + "<p><secText>The legal statement is generated. "
        + "You can review, change any details, "
        + "then sign and submit your application.</secText></p>"
        + "<p><secText>Cynhyrchwyd y datganiad cyfreithiol.  "
        + "Gallwch adolygu, newid unrhyw fanylion, llofnodi a chyflwyno eich cais.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><paymentTabLink/></p>"
        + "<p><paymentTabLinkWelsh/></p>"
        + "<p><secText><paymentHintText/></secText></p>"
        + "<p><secText><paymentHintTextWelsh/></secText></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-paymentMade/>"
        + CLOSE_GRID_ROW
        + SEPARATOR
        + "<sendDocs/>"
        + "<br/>\n"
        + "<h>3. Review application</h>\n"
        + "<h>3. Adolygu'r cais</h>\n"
        + OPEN_ROW
        + "<p><secText>These steps are completed by HM Courts and "
        + "Tribunals Service staff. It can take a few weeks before the review starts.</secText></p>"
        + "<p><secText>Dylai'r camau hyn gael eu cwblhau gan staff Gwasanaeth Llysoedd a Thribiwnlysoedd "
        + "EF. Gall gymryd ychydig wythnosau cyn i'r adolygiad ddechrau.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><authDocsLink/></p>"
        + "<p><authDocsLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-authDocuments/>"
        + CLOSE_GRID_ROW
        + "<authenticatedDate/>"
        + OPEN_ROW
        + "<p><secText>We will authenticate your documents and "
        + "match them with your application.</secText></p>"
        + "<p><secText>Byddwn yn dilysu eich dogfennau ac yn eu paru â'ch cais.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><examAppLink/></p>"
        + "<p><examAppLinkWelsh/></p>"
        + CLOSE_COL1_OPEN_COL2
        + "<status-examineApp/>"
        + CLOSE_GRID_ROW
        + OPEN_ROW
        + "<p><secText>We review your application for incomplete information or problems and validate it "
        + "against other cases or caveats. After the review we prepare the grant.</secText></p>"
        + "<p><secText>Byddwn yn adolygu eich cais am wybodaeth anghyflawn neu broblemau ac yn ei ddilysu "
        + "yn erbyn achosion eraill neu gafeatau. Ar ôl yr adolygiad, byddwn yn paratoi'r grant.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + OPEN_ROW
        + "<p><secText>Your application will update through any of these case states as it is "
        + "reviewed by our team:</secText></p>"
        + "<p><secText>Bydd eich cais yn cael ei ddiweddaru ac yn symud drwy'r camau hyn fel y bydd yn cael ei "
        + "adolygu gan ein tîm:</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + "<ul>\n<li>Examining</li>\n<li>Archwilio</li>\n"
        + "<li>Case Matching</li>\n<li>Paru Achos</li>\n"
        + "<li>Case selected for Quality Assurance</li>\n<li>Achos wedi’i ddethol ar gyfer Sicrhau Ansawdd</li>\n"
        + "<li>Ready to issue</li>\n<li>Barod i’w gychwyn</li>\n</ul>"
        + SEPARATOR
        + "<h>4. Grant of representation</h>\n"
        + "<h>4. Grant cynrychiolaeth</h>\n"
        + OPEN_ROW
        + "<p><secText>This step is completed by HM Courts and Tribunals Service staff.</secText></p>"
        + "<p><secText>Dylai'r cam hwn gael ei gwblhau gan staff Gwasanaeth Llysoedd a Thribiwnlysoedd EF."
        + "</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + OPEN_ROW
        + "<p><issueGrantLink/>/p>"
        + "<p><issueGrantLinkWelsh/>/p>"
        + "</gridCol-two-thirds><gridCol-one-third><status-issueGrant/>"
        + CLOSE_GRID_ROW
        + OPEN_ROW
        + "<p><secText>The grant will be delivered in the post a few days after issuing.</secText></p>"
        + "<p><secText>Bydd y grant yn cael ei anfon yn y post ychydig ddyddiau ar ôl ei gyhoeddi.</secText></p>"
        + CLOSE_GRID_ROW_WITH_EMPTY_COLUMN
        + SEPARATOR
        + "</gridCol-two-thirds>\n"
        + "</gridRow>\n";

    private CaseTaskListHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
