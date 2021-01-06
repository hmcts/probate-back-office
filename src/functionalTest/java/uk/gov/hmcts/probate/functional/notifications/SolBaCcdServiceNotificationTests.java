package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolBaCcdServiceNotificationTests extends IntegrationTestBase {

    private static final String PA_STOP_DETAILS = "PA stop details";
    private static final String SOLS_STOP_DETAILS = "SOLS stop details";

    private static final String DOCUMENTS_RECEIVED = "/notify/documents-received";
    private static final String GRANT_ISSUED = "/document/generate-grant";
    private static final String GRANT_REISSUED = "/document/generate-grant-reissue";
    private static final String CASE_STOPPED = "/notify/case-stopped";
    private static final String INFORMATION_REQUEST_DEFAULT_VALUES = "/notify/request-information-default-values";
    private static final String INFORMATION_REQUEST = "/notify/stopped-information-request";
    private static final String GRANT_RAISED = "/notify/grant-received";
    private static final String APPLICATION_RECEIVED = "/notify/application-received";
    private static final String PAPER_FORM = "/case/paperForm";

    private static final String BIRMINGHAM_NO = "0121 681 3401";

    private static final String EMAIL_NOTIFICATION_URL = "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String GENERATED_DOCUMENT_URL = "data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String EMAIL_NOTIFICATION_DOCUMENT_URL = "DocumentLink.document_binary_url";

    @Test
    public void verifyCitizenPaperApplicationReceivedByCaseworkerNotificationSent() {
        postNotificationEmailAndVerifyContents(PAPER_FORM, "paperApplicationRecievedCitizenFromCaseworkerPayload.json", "paperApplicationReceivedCitizenFromCaseworkerEmailExpectedResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifyGrantReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadGrantReissued.json",
            "expectedPersonalDocumentGrantReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }

    @Test
    public void verifyIntestacyReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED,"personalPayloadIntestacyReissued.json",
            "expectedPersonalDocumentIntestacyReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }

    @Test
    public void verifyAdmonWillReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED,"personalPayloadAdmonWillReissued.json",
            "expectedPersonalDocumentAdmonWillReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }
    
    @Test
    public void verifyWelshGrantReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED,"personalPayloadWelshGrantReissued.json",
            "expectedPersonalDocumentWelshGrantReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyWelshIntestacyReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED,"personalPayloadWelshIntestacyReissued.json",
            "expectedPersonalDocumentWelshIntestacyReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyWelshAdmonWillReissueDocumentAndEmail() {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED,"personalPayloadWelshAdmonWillReissued.json", 
            "expectedPersonalDocumentWelshAdmonWillReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyDigitalGOPApplicationReceivedNotificationEmailText() {
        ResponseBody responseBody = validatePostSuccess("digitalApplicationRecievedPayload.json", APPLICATION_RECEIVED);
        assertExpectedContents("digitalApplicationRecievedEmailResponse.txt", "DocumentLink.document_binary_url", responseBody);
    }

    @Test
    public void verifyDigitalIntestacyApplicationReceivedNotificationSent() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("digitalApplicationRecievedPayload.json", APPLICATION_RECEIVED,
            "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyPaperApplicationReceivedNotificationSentForNullInPaperForm() {
        ResponseBody responseBody = validatePostSuccess("paperApplicationRecievedPayloadForCitizen.json", APPLICATION_RECEIVED);
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyPaperApplicationReceivedNotificationNotSent() {
        ResponseBody responseBody = validatePostSuccess("paperApplicationRecievedPayload.json", APPLICATION_RECEIVED);
        assertTrue(!responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyDigitalPaperFormGOPApplicationReceivedNotificationEmailTextWelsh() {
        postNotificationEmailAndVerifyContents(APPLICATION_RECEIVED, "digitalApplicationRecievedPayloadWelsh.json", "digitalApplicationRecievedExpectedResonseWelsh.txt",
            EMAIL_NOTIFICATION_DOCUMENT_URL);
    }

    @Test
    public void verifyDigitalPaperFormGOPApplicationReceivedNotificationEmailTextSolicitorWelsh() {
        postNotificationEmailAndVerifyContents(APPLICATION_RECEIVED, "digitalApplicationRecievedPayloadSolicitorWelsh.json", "digitalApplicationRecievedExpectedResonseSolicitorWelsh.txt",
            EMAIL_NOTIFICATION_DOCUMENT_URL);
    }

    @Test
    public void verifyPersonalApplicantDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", DOCUMENTS_RECEIVED);
    }

    @Test
    public void verifyPersonalApplicantDocumentReceivedContentIsOk() {
        String document = sendEmail("personalPayloadNotifications.json", DOCUMENTS_RECEIVED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    @Test
    public void verifySolicitorApplicantDocumentReceivedContentIsOk() {
        String document = sendEmail("solicitorPayloadNotificationsBirmingham.json", DOCUMENTS_RECEIVED, EMAIL_NOTIFICATION_URL);
        verifySolsEmailNotificationReceived(document);
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_ISSUED);
    }

    @Test
    public void verifySolicitorGrantRaisedShouldReturnOkResponseCode() {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "solicitorPayloadNotifications.json",
            "grantRaisedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantRaisedIntestacyShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
            "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("grantRaisedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantRaisedAdmonWillShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
            "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantRaisedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() {
        postNotificationEmailAndVerifyContents(DOCUMENTS_RECEIVED, "solicitorPayloadNotifications.json",
            "documentReceivedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorDocumentsReceivedIntestacyShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
            "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("documentReceivedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorDocumentsReceivedAdmonWillShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
            "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("documentReceivedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantIssuedShouldReturnOkResponseCode() {
        postNotificationEmailAndVerifyContents(GRANT_ISSUED, "solicitorPayloadNotifications.json",
            "grantIssuedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantIssuedIntestacyShouldReturnOkResponseCode() {
        postNotificationEmailAndVerifyContents(GRANT_ISSUED, "solicitorPayloadNotificationsIntestacy.json",
            "grantIssuedIntestacySolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantIssuedAdmonWillShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_ISSUED,
            "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantIssuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantReissuedShouldReturnOkResponseCode() {
        postNotificationEmailAndVerifyContents(GRANT_REISSUED, "solicitorPayloadNotifications.json",
            "grantReissuedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }
    
    @Test
    public void verifySolicitorGrantReissuedIntestacyShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
            "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("grantReissuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }
    
    @Test
    public void verifySolicitorGrantReissuedAdmonWillShouldReturnOkResponseCode() {
        ResponseBody responseBody = validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
            "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantReissuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifyPersonalApplicantGrantReissuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_REISSUED);
    }

    @Test
    public void verifyPersonalApplicantGrantRaisedWithEmailShouldReturnOkResponseCode() {
        validatePostSuccess("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    public void verifyPersonalApplicantGrantRaisedWithoutEmailShouldReturnOkResponseCode() {
        validatePostSuccess("personalRaiseGrantWithoutEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    public void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailText() {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanPayload.json", "grantRaisedPaperBulkScanEmailExpectedResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailText() {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanSolicitorPayload.json", "grantRaisedPaperBulkScanEmailExpectedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
        public void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanPayloadWelsh.json", "grantRaisedPaperBulkScanEmailExpectedWelshResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanSolicitorPayloadWelsh.json", "grantRaisedPaperBulkScanEmailExpectedSolicitorWelshResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifyPersonalApplicantGrantReceivedContentIsOk() {
        String document = sendEmail("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedContentIsOk() {
        String document = sendEmail("personalPayloadNotifications.json", GRANT_ISSUED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    @Test
    public void verifySolicitorApplicantGrantIssuedContentIsOk() {
        String document = sendEmail("solicitorPayloadNotificationsBirmingham.json", GRANT_ISSUED, EMAIL_NOTIFICATION_URL);
        verifySolsEmailNotificationReceived(document);
    }

    @Test
    public void verifySolicitorCaseStoppedShouldReturnOkResponseCode() {
        String document = sendEmail("solicitorPayloadNotifications.json", CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        assertTrue(document.contains(SOLS_STOP_DETAILS));
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedShouldReturnOkResponseCode() {
        String document = sendEmail("personalPayloadNotifications.json", CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        assertTrue(document.contains(PA_STOP_DETAILS));
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedContentIsOk() {
        String document = sendEmail("personalPayloadNotifications.json", CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailCaseStopped(document);
    }

    @Test
    public void verifySolicitorCaseStoppedContentIsOkay() {
        String document = sendEmail("solicitorPayloadNotificationsBirmingham.json", CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        verifySolsEmailCaseStopped(document);
    }

    @Test
    public void verifySpecialCharacterEncodingIsOk() {
        String document = sendEmail("personalPayloadNotificationsSpecialCharacters.json", CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailCaseStopped(document);
        assertTrue(document.contains("!@£$%^&*()[]{}<>,.:;~"));
    }

    @Test
    public void verifyPersonalApplicantRequestInformationEmailContentIsOk() {
        String document = sendEmail("personalPayloadNotifications.json", INFORMATION_REQUEST, EMAIL_NOTIFICATION_URL);
        verifyPAEmailInformationRequestRedec(document);
    }

    @Test
    public void verifyPersonalApplicantRequestInformationDefaultValuesIsOk() {
        validatePostSuccess("personalPayloadNotifications.json", INFORMATION_REQUEST_DEFAULT_VALUES);
    }

    private String sendEmail(String fileName, String url, String jsonDocumentUrl) {
        ResponseBody body = validatePostSuccess(fileName, url);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String documentUrl = jsonPath.get(jsonDocumentUrl);

        String document = utils.downloadPdfAndParseToString(documentUrl);
        return document;
    }

    private void verifyPAEmailNotificationReceived(String document){
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailNotificationReceived(String document){
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Solicitor_fn Solicitor_ln"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailCaseStopped(String document){
        assertTrue(document.contains(SOLS_STOP_DETAILS));
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Solicitor_fn Solicitor_ln"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("1st January 2000"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifyPAEmailCaseStopped(String document){
        assertTrue(document.contains(PA_STOP_DETAILS));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("1st January 2000"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifyPAEmailInformationRequestRedec(String document) {
        assertTrue(document.contains("primary@probate-test.com"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains(BIRMINGHAM_NO));
        assertTrue(document.contains("Declaration"));
    }

    private void postNotificationEmailAndVerifyContents(String apiPath, String jsonPayloadFile, String expectedResponseFile,
                                                        String responseDocumentUrl) {
        ResponseBody responseBody = validatePostSuccess(jsonPayloadFile, apiPath);
        assertExpectedContents(expectedResponseFile, responseDocumentUrl, responseBody);
    }

    private void verifyDocumentAndEmailNotificationGenerated(String api, String payload, String documentText, String emailText) {
        ResponseBody responseBody = validatePostSuccess(payload, api);
        assertExpectedContents(documentText, GENERATED_DOCUMENT_URL, responseBody);
        assertExpectedContents(emailText, EMAIL_NOTIFICATION_URL, responseBody);
    }

    private void verifyEmailGenerated(JsonPath jsonPath, String emailText) {
        String expectedEmailText = utils.getJsonFromFile(emailText);
        expectedEmailText = expectedEmailText.replace("\n", "").replace("\r", "");

        String emailUrl = jsonPath.get(EMAIL_NOTIFICATION_URL);
        String response = utils.downloadPdfAndParseToString(emailUrl);
        response = response.replace("\n", "").replace("\r", "");
        assertTrue(response.contains(expectedEmailText));
    }
}