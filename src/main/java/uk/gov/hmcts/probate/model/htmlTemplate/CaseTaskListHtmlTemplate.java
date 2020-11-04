package uk.gov.hmcts.probate.model.htmlTemplate;

public class CaseTaskListHtmlTemplate {
    public static String taskListTemplate =
            //"<style>.govuk-tag--custom-status--grey,.govuk-tag--custom-status--blue{line-height:3;margin-top:0.4rem};.govuk-tag--custom-status--grey{color:#383f43;background-color:#eeefef};.govuk-tag--custom-status-blue{color:#144e81;background-color:#d2e2f1}</style><h>1. Enter application details</h>\n" +
            "<h>1. Enter application details</h>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>These steps are to be completed by the legal professional.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><addSolicitorLink/></p></gridCol-two-thirds><gridCol-one-third><status-addSolicitor/></gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><addDeceasedLink/></p></gridCol-two-thirds><gridCol-one-third><status-addDeceasedDetails/></gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><addAppLink/></p></gridCol-two-thirds><gridCol-one-third><status-addApplicationDetails/></gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<br/>\n" +
            "<h>2. Sign legal statement and submit application</h>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>These steps are to be completed by the legal professional.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><rvwLink/></p></gridCol-two-thirds><gridCol-one-third><status-reviewAndSubmit/></gridCol-one-third></gridRow>\n" +
            "<reviewAndSubmitDate/>" +
            "<gridRow><gridCol-two-thirds><p><secText>The legal statement is generated. You can review, change any details, then sign and submit your application.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p>Send documents<br/></p></gridCol-two-thirds><gridCol-one-third><status-sendDocuments/></gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<br/>\n" +
            "<h>3. Review application</h>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><authDocsLink/></p></gridCol-two-thirds><gridCol-one-third><status-authDocuments/></gridCol-one-third></gridRow>\n" +
            "<authenticatedDate/>" +
            "<gridRow><gridCol-two-thirds><p><secText>We will authenticate your documents and match them with your application.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><examAppLink/></p></gridCol-two-thirds><gridCol-one-third><status-examineApp/></gridCol-one-third></gridRow>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>Your application will update through any of these case states as it is reviewed by our team:</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<ul>\n<li>Examining</li>\n<li>Case Matching</li>\n<li>Case selected for Quality Assurance</li>\n<li>Ready to issue</li>\n</ul>" +
            "<gridRowSeparator>\n" +
            "<h>4. Grant of representation</h>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>This step is completed by HM Courts and Tribunals Service staff.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n" +
            "<gridRow><gridCol-two-thirds><p><issueGrantLink/>/p></gridCol-two-thirds><gridCol-one-third><status-issueGrant/></gridCol-one-third></gridRow>\n" +
            "<gridRow><gridCol-two-thirds><p><secText>The grant will be delivered in the post a few days after issuing.</secText></p></gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n" +
            "<gridRowSeparator>\n";

}
