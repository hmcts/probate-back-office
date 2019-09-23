package uk.gov.hmcts.probate.functional.notifications;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertTrue;


@RunWith(SerenityRunner.class)
public class SolBaCcdServiceNotificationTests extends IntegrationTestBase {

    private static final String PA_STOP_DETAILS = "PA stop details";
    private static final String SOLS_STOP_DETAILS = "SOLS stop details";

    private static final String DOCUMENTS_RECEIVED = "/notify/documents-received";
    private static final String GRANT_ISSUED = "/document/generate-grant";
    private static final String GRANT_REISSUED = "/document/generate-grant-reissue";
    private static final String CASE_STOPPED = "/notify/case-stopped";
    private static final String INFORMATION_REQUEST_DEFAULT_VALUES = "/notify/request-information-default-values";
    private static final String INFORMATION_REQUEST = "/notify/stopped-information-request";
    private static final String REDEC_SOT_URL = "/notify/redeclaration-sot";

    private static final String BIRMINGHAM_NO = "0121 681 3401";

    private static final String EMAIL_NOTIFICATION_URL = "data.probateNotificationsGenerated[0].value.DocumentLink.document_binary_url";

    @Test
    public void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", DOCUMENTS_RECEIVED);
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
    public void verifySolicitorGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", GRANT_ISSUED);
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_ISSUED);
    }

    @Test
    public void verifySolicitorGrantReissuedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", GRANT_REISSUED);
    }

    @Test
    public void verifyPersonalApplicantGrantReissuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", GRANT_REISSUED);
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

    @Test
    public void verifyPersonalApplicantRedecSoTIsOk() {
       // validatePostSuccess("personalPayloadNotifications.json", REDEC_SOT_URL);
        String document = sendEmail("personalPayloadNotifications.json", REDEC_SOT_URL, EMAIL_NOTIFICATION_URL);
        verifyPAEmailNotificationReceived(document);
    }

    //TODO: uncomment when letters are being used again
    //@Test
    //public void verifyPersonalApplicantRequestInformationLetterContentIsOk() {
    //    String coversheet = generateDocument("personalPayloadNotificationsNoEmailRequested.json", INFORMATION_REQUEST,
    //            0);
    //    String letter = generateDocument("personalPayloadNotificationsNoEmailRequested.json", INFORMATION_REQUEST,
    //            1);
    //    verifyPALetterInformationRequestRedec(letter);
    //}


    private String sendEmail(String fileName, String url, String jsonDocumentUrl) {
        ResponseBody body = validatePostSuccess(fileName, url);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String documentUrl = jsonPath.get(jsonDocumentUrl);

        String document = utils.downloadPdfAndParseToString(documentUrl);
        return document;
    }

    private ResponseBody validatePostSuccess(String jsonFileName, String path) {
        Response response = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .andReturn();

        response.then().assertThat().statusCode(200);

        return response.getBody();
    }

    private void verifyPAEmailNotificationReceived(String document){
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailNotificationReceived(String document){
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("name"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains(BIRMINGHAM_NO));
    }

    private void verifySolsEmailCaseStopped(String document){
        assertTrue(document.contains(SOLS_STOP_DETAILS));
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("name"));
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
        assertTrue(document.contains("test@test.com"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains(BIRMINGHAM_NO));
        assertTrue(document.contains("Declaration"));
    }

    private void verifyPALetterInformationRequestRedec(String document) {
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
        assertTrue(document.contains("Executor name 1 "));
        assertTrue(document.contains("1st January 2000"));
        assertTrue(document.contains("PA stop details"));
        assertTrue(document.contains("Declaration"));
    }

    private String generateDocument(String jsonFileName, String path, int placeholder) {

        Response jsonResponse = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.probateDocumentsGenerated["
                + placeholder
                + "].value.DocumentLink.document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }
}