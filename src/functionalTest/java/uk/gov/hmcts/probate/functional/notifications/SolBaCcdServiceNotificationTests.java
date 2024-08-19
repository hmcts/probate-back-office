package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
public class SolBaCcdServiceNotificationTests extends IntegrationTestBase {

    private static final String SOLS_STOP_DETAILS = "SOLS stop details";

    private static final String DOCUMENTS_RECEIVED = "/notify/documents-received";
    private static final String GRANT_ISSUED = "/document/generate-grant";
    private static final String GRANT_REISSUED = "/document/generate-grant-reissue";
    private static final String CASE_STOPPED = "/notify/case-stopped";
    private static final String INFORMATION_REQUEST_DEFAULT_VALUES = "/notify/request-information-default-values";
    private static final String INFORMATION_REQUEST = "/notify/stopped-information-request";
    private static final String GRANT_RAISED = "/notify/grant-received";
    private static final String START_GRANT_DELAYED = "/notify/start-grant-delayed-notify-period";
    private static final String APPLICATION_RECEIVED = "/notify/application-received";
    private static final String PAPER_FORM = "/case/paperForm";

    private static final String BIRMINGHAM_NO = "0300 303 0648";
    private static final String REGISTRY_NO = "0113 389 6133";

    private static final String EMAIL_NOTIFICATION_URL =
        "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String GENERATED_DOCUMENT_URL =
        "data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String EMAIL_NOTIFICATION_DOCUMENT_URL = "DocumentLink.document_binary_url";
    private static final String CAVEAT_JSON = "/caveat/createCaveatSolicitor.json";
    private static final String EVENT_PARAMETER = "EVENT_PARM";
    private static final String CAVEAT_RAISE_EVENT = "raiseCaveat";

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyCitizenPaperApplicationReceivedByCaseworkerNotificationSent() throws IOException {
        postNotificationEmailAndVerifyContents(PAPER_FORM, "paperApplicationRecievedCitizenFromCaseworkerPayload.json",
            "paperApplicationReceivedCitizenFromCaseworkerEmailExpectedResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifyGrantReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadGrantReissued.json",
            "expectedPersonalDocumentGrantReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }

    @Test
    public void verifyIntestacyReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadIntestacyReissued.json",
            "expectedPersonalDocumentIntestacyReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }

    @Test
    public void verifyAdmonWillReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadAdmonWillReissued.json",
            "expectedPersonalDocumentAdmonWillReissued.txt",
            "expectedPersonalEmailGrantReissued.txt");
    }

    @Test
    public void verifyGrantReissueDocumentAppNameWithApostrophe() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
            "personalPayloadGrantReissuedNameWithApostrophe.json", GRANT_REISSUED);
        assertExpectedContents("expectedPersonalDocumentGrantReissuedNameWithApostrophe.txt",
            GENERATED_DOCUMENT_URL, responseBody);
    }

    @Test
    public void verifyGrantReissueDocumentAppNameDoubleBarrelled() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
            "personalPayloadGrantReissuedNameDoubleBarrelled.json", GRANT_REISSUED);
        assertExpectedContents("expectedPersonalDocumentGrantReissuedNameDoubleBarrelled.txt",
            GENERATED_DOCUMENT_URL, responseBody);
    }

    @Test
    public void verifyWelshGrantReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadWelshGrantReissued.json",
            "expectedPersonalDocumentWelshGrantReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyWelshIntestacyReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadWelshIntestacyReissued.json",
            "expectedPersonalDocumentWelshIntestacyReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyWelshAdmonWillReissueDocumentAndEmail() throws IOException {
        verifyDocumentAndEmailNotificationGenerated(GRANT_REISSUED, "personalPayloadWelshAdmonWillReissued.json",
            "expectedPersonalDocumentWelshAdmonWillReissued.txt",
            "expectedPersonalEmailWelshGrantReissued.txt");
    }

    @Test
    public void verifyWelshGrantReissueDocumentAppNameWithApostrophe() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
            "personalPayloadWelshGrantReissuedNameWithApostrophe.json", GRANT_REISSUED);
        assertExpectedContents("expectedPersonalDocumentWelshGrantReissuedNameWithApostrophe.txt",
            GENERATED_DOCUMENT_URL, responseBody);
    }

    @Test
    public void verifyWelshGrantReissueDocumentAppNameDoubleBarrelled() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(
            "personalPayloadWelshGrantReissuedNameDoubleBarrelled.json", GRANT_REISSUED);
        assertExpectedContents("expectedPersonalDocumentWelshGrantReissuedNameDoubleBarrelled.txt",
            GENERATED_DOCUMENT_URL, responseBody);
    }

    @Test
    public void verifyDigitalGOPApplicationReceivedNotificationEmailText() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("digitalApplicationRecievedPayload.json",
                APPLICATION_RECEIVED);
        assertExpectedContents("digitalApplicationRecievedEmailResponse.txt",
                "DocumentLink.document_binary_url",
            responseBody);
    }

    @Test
    public void verifyDigitalIntestacyApplicationReceivedNotificationSent() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("digitalApplicationRecievedPayload.json",
                    APPLICATION_RECEIVED,"\"caseType\":\"gop\"",
                "\"caseType\":\"intestacy\"");
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyPaperApplicationReceivedNotificationSentForNullInPaperForm() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccess("paperApplicationRecievedPayloadForCitizen.json", APPLICATION_RECEIVED);
        assertTrue(responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyPaperApplicationReceivedNotificationNotSent() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("paperApplicationRecievedPayload.json",
                APPLICATION_RECEIVED);
        assertTrue(!responseBody.asString().contains("DocumentLink"));
    }

    @Test
    public void verifyDigitalPaperFormGOPApplicationReceivedNotificationEmailTextWelsh() throws IOException {
        postNotificationEmailAndVerifyContents(APPLICATION_RECEIVED, "digitalApplicationRecievedPayloadWelsh.json",
            "digitalApplicationRecievedExpectedResonseWelsh.txt",
            EMAIL_NOTIFICATION_DOCUMENT_URL);
    }

    @Test
    public void verifyDigitalPaperFormGOPApplicationReceivedNotificationEmailTextSolicitorWelsh() throws IOException {
        postNotificationEmailAndVerifyContents(APPLICATION_RECEIVED,
            "digitalApplicationRecievedPayloadSolicitorWelsh.json",
            "digitalApplicationRecievedExpectedResonseSolicitorWelsh.txt",
            EMAIL_NOTIFICATION_DOCUMENT_URL);
    }

    @Test
    public void verifyPersonalApplicantDocumentsReceivedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", DOCUMENTS_RECEIVED);
    }

    @Ignore // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    public void verifyPersonalApplicantDocumentReceivedContentIsOk() throws IOException {
        final String document = sendEmail("personalPayloadNotifications.json", DOCUMENTS_RECEIVED,
                EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    @Ignore // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    public void verifySolicitorApplicantDocumentReceivedContentIsOk() throws IOException {
        final String document =
            sendEmail("solicitorPayloadNotificationsBirmingham.json", DOCUMENTS_RECEIVED,
                    EMAIL_NOTIFICATION_URL);
        verifySolsEmailNotificationReceived(document);
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_ISSUED);
    }

    @Test
    public void verifySolicitorGrantRaisedShouldReturnOkResponseCode() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "solicitorPayloadNotifications.json",
            "grantRaisedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantRaisedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("grantRaisedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantRaisedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_RAISED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantRaisedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Ignore // tech decision to be made if have these conditional on launch darkly toggle or remove permantently
    @Test
    public void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() throws IOException {
        postNotificationEmailAndVerifyContents(DOCUMENTS_RECEIVED, "solicitorPayloadNotifications.json",
            "documentReceivedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Ignore
    @Test
    public void verifySolicitorDocumentsReceivedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("documentReceivedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Ignore
    @Test
    public void verifySolicitorDocumentsReceivedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("documentReceivedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantIssuedShouldReturnOkResponseCode() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_ISSUED, "solicitorPayloadNotifications.json",
            "grantIssuedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantIssuedIntestacyShouldReturnOkResponseCode() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_ISSUED, "solicitorPayloadNotificationsIntestacy.json",
            "grantIssuedIntestacySolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantIssuedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_ISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantIssuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL, responseBody);
    }

    @Test
    public void verifySolicitorGrantReissuedShouldReturnOkResponseCode() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_REISSUED, "solicitorPayloadNotifications.json",
            "grantReissuedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorGrantReissuedIntestacyShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"intestacy\"");
        assertExpectedContents("grantReissuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL,
                responseBody);
    }

    @Test
    public void verifySolicitorGrantReissuedAdmonWillShouldReturnOkResponseCode() throws IOException {
        final ResponseBody responseBody =
            validatePostSuccessWithAttributeUpdate("solicitorPayloadNotifications.json", GRANT_REISSUED,
                "\"caseType\":\"gop\"", "\"caseType\":\"admonWill\"");
        assertExpectedContents("grantReissuedSolicitorResponse.txt", EMAIL_NOTIFICATION_URL,
                responseBody);
    }

    @Test
    public void verifyPersonalApplicantGrantReissuedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_REISSUED);
    }

    @Test
    public void verifyPersonalApplicantGrantRaisedWithEmailShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    public void verifyPersonalApplicantGrantRaisedWithoutEmailShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess("personalRaiseGrantWithoutEmailNotifications.json", GRANT_RAISED);
    }

    @Test
    public void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailText() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanPayload.json",
            "grantRaisedPaperBulkScanEmailExpectedResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailText() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanSolicitorPayload.json",
            "grantRaisedPaperBulkScanEmailExpectedSolicitorResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifyBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanPayloadWelsh.json",
            "grantRaisedPaperBulkScanEmailExpectedWelshResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifySolicitorBulkScanPaperFormGOPGrantReceivedNotificationEmailTextWelsh() throws IOException {
        postNotificationEmailAndVerifyContents(GRANT_RAISED, "grantRaisedPaperBulkScanSolicitorPayloadWelsh.json",
            "grantRaisedPaperBulkScanEmailExpectedSolicitorWelshResponse.txt",
            EMAIL_NOTIFICATION_URL);
    }

    @Test
    public void verifyPersonalApplicantGrantReceivedContentIsOk() throws IOException {
        final String document =
            sendEmail("personalRaiseGrantWithEmailNotifications.json", GRANT_RAISED, EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedContentIsOk() throws IOException {
        final String document = sendEmail("personalPayloadNotifications.json", GRANT_ISSUED,
                EMAIL_NOTIFICATION_URL);
        String expectedText = utils.getStringFromFile("/json/personalApplicantGrantIssuedEmailContent.txt");
        assertTrue(document.contains(expectedText));
    }

    @Test
    public void verifySolicitorApplicantGrantIssuedContentIsOk() throws IOException {
        final String document =
            sendEmail("solicitorPayloadNotificationsBirmingham.json", GRANT_ISSUED, EMAIL_NOTIFICATION_URL);
        verifySolsEmailNotificationReceived(document);
    }

    @Test
    public void verifySolicitorCaseStoppedShouldReturnOkResponseCode() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("solicitorPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        final String document = sendEmailForCaseStopped(payload, CASE_STOPPED, EMAIL_NOTIFICATION_URL);
        verifySolsEmailCaseStopped(document);
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedContentIsOk() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("personalPayloadNotifications.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        final String document = sendEmailForCaseStopped(payload, CASE_STOPPED,
                EMAIL_NOTIFICATION_URL);
        verifyPAEmailCaseStopped(document);
    }

    @Test
    public void verifySpecialCharacterEncodingIsOk() throws IOException, InterruptedException {
        String caseId = createCase();
        String payload = utils.getJsonFromFile("personalPayloadNotificationsSpecialCharacters.json");
        payload = replaceAllInString(payload, "\"boCaseStopCaveatId\": \"1691481848274878\",",
                "\"boCaseStopCaveatId\": \"" + caseId + "\",");
        final String document =
                sendEmailForCaseStopped(payload, CASE_STOPPED,
                    EMAIL_NOTIFICATION_URL);
        verifyPAEmailCaseStopped(document);
    }

    @Test
    public void verifyPersonalApplicantRequestInformationEmailContentIsOk() throws IOException {
        final String document = sendEmail("personalPayloadNotifications.json", INFORMATION_REQUEST,
                EMAIL_NOTIFICATION_URL);
        verifyPAEmailInformationRequestRedec(document);
    }

    @Test
    public void verifyPersonalApplicantRequestInformationDefaultValuesIsOk() throws IOException {
        validatePostSuccess("personalPayloadNotifications.json", INFORMATION_REQUEST_DEFAULT_VALUES);
    }

    @Test
    public void verifyStartGrantDelayed() throws IOException {
        final ResponseBody responseBody = validatePostSuccess("personalRaiseGrantWithEvidenceHandledNo.json",
                START_GRANT_DELAYED);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());
        assertNotNull(jsonPath.get("data.lastEvidenceAddedDate"));
        assertNotNull(jsonPath.get("data.grantDelayedNotificationDate"));
        assertNull(jsonPath.get("data.grantAwaitingDocumentationNotificationDate"));
    }

    private String sendEmail(String fileName, String url, String jsonDocumentUrl) throws IOException {
        final ResponseBody body = validatePostSuccess(fileName, url);

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String documentUrl = jsonPath.get(jsonDocumentUrl);

        final String document = removeLineFeeds(utils.downloadPdfAndParseToString(documentUrl));
        return document;
    }

    private String sendEmailForCaseStopped(String fileName, String url, String jsonDocumentUrl) throws IOException,
            InterruptedException {
        final ResponseBody body = validatePostSuccessForCaseStopped(fileName, url);

        final JsonPath jsonPath = JsonPath.from(body.asString());
        final String documentUrl = jsonPath.get(jsonDocumentUrl);

        final String document = removeLineFeeds(utils.downloadPdfAndParseToString(documentUrl));
        return document;
    }

    private void verifyPAEmailNotificationReceived(String document) {
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailNotificationReceived(String document) {
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Solicitor_fn Solicitor_ln"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailCaseStopped(String document) {
        assertTrue(document.contains("Solicitor_fn Solicitor_ln"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("cav first name cav surname"));
        assertTrue(document.contains(REGISTRY_NO));
    }

    private void verifyPAEmailCaseStopped(String document) {
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("cav first name cav surname"));
        assertTrue(document.contains(REGISTRY_NO));
    }

    private void verifyPAEmailInformationRequestRedec(String document) {
        assertTrue(document.contains("primary@probate-test.com"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains(BIRMINGHAM_NO));
        assertTrue(document.contains("Declaration"));
    }

    private void postNotificationEmailAndVerifyContents(String apiPath, String jsonPayloadFile,
                                                        String expectedResponseFile,
                                                        String responseDocumentUrl) throws IOException {
        final ResponseBody responseBody = validatePostSuccess(jsonPayloadFile, apiPath);
        assertExpectedContents(expectedResponseFile, responseDocumentUrl, responseBody);
    }

    private void verifyDocumentAndEmailNotificationGenerated(String api, String payload, String documentText,
                                                             String emailText) throws IOException {
        final ResponseBody responseBody = validatePostSuccess(payload, api);
        assertExpectedContents(documentText, GENERATED_DOCUMENT_URL, responseBody);
        assertExpectedContents(emailText, EMAIL_NOTIFICATION_URL, responseBody);
    }

    public String createCase() throws IOException {
        //Create Case
        final String baseCaseJson = utils.getJsonFromFile(CAVEAT_JSON);
        final String applyForCaveatCaseJson = utils.replaceAttribute(baseCaseJson, EVENT_PARAMETER, CAVEAT_RAISE_EVENT);
        final String applyForGrantCase = utils.createCaveatCaseAsCaseworker(applyForCaveatCaseJson, CAVEAT_RAISE_EVENT);
        final JsonPath jsonPathApply = JsonPath.from(applyForGrantCase);
        final String caseId = jsonPathApply.get("id").toString();
        log.info("createCase : caseId {} ", caseId);
        return caseId;
    }
}
