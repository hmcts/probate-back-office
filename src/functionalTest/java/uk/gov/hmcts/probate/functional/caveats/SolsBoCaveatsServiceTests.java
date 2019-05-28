package uk.gov.hmcts.probate.functional.caveats;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class SolsBoCaveatsServiceTests extends IntegrationTestBase {

    private static final String CAVEAT_RAISED = "/caveat/raise";
    private static final String CAVEAT_DEFAULT_VALUES = "/caveat/defaultValues";
    private static final String CAVEAT_GENERAL_MESSAGE= "/caveat/general-message";

    private static final String DEFAULT_PAYLOAD= "caveatPayloadNotifications.json";
    private static final String DEFAULT_PAYLOAD_NO_EMAIL= "caveatPayloadNotificationsNoEmail.json";
    private static final String DEFAULT_PAYLOAD_CTSC= "caveatPayloadNotificationsNoEmailCTSC.json";

    private static final String YES = "Yes";
    private static final String NO = "No";

    @Test
    public void verifyCaveatRaisedShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_RAISED);
    }

    @Test
    public void verifyCaveatDefaultValuesShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_DEFAULT_VALUES);
    }

    @Test
    public void verifyCaveatGeneralMessageShouldReturnOkResponseCode() {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_GENERAL_MESSAGE);
    }


    @Test
    public void verifySuccessForCaveatDefaultValuesWithEmail() {
        String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);


        JsonPath jsonPath = JsonPath.from(response);
        String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(YES, emailRequested);
        assertEquals(NO, bulkPrintRequested);

    }


    @Test
    public void verifySuccessForCaveatDefaultValuesWithoutEmail() {
        String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_DEFAULT_VALUES);

        JsonPath jsonPath = JsonPath.from(response);
        String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(NO, emailRequested);
        assertEquals(YES, bulkPrintRequested);

    }


    @Test
    public void verifySuccessForCaveatRaisedEmail() {
        String response = generateDocument(DEFAULT_PAYLOAD, CAVEAT_RAISED, 0);

        assertCommons(response);
        assertTrue(response.contains("1542274092932452"));
        assertTrue(response.contains("personal@hmcts-test.com"));

    }

    @Test
    public void verifySuccessForCaveatRaisedDocumentAndCoversheet() {
        String coversheet = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 0);
        String response = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertAddress(coversheet);

        assertTrue(!response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));

    }

    @Test
    public void verifySuccessForCaveatRaisedDocumentAndCoversheetCTSC() {
        String coversheet = generateDocument(DEFAULT_PAYLOAD_CTSC, CAVEAT_RAISED, 0);
        String response = generateDocument(DEFAULT_PAYLOAD_CTSC, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertTrue(response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));
        assertAddress(coversheet);

    }


    private void assertCommons(String response) {

        assertTrue(response.contains("cf name 2 cl name 2"));
        assertTrue(response.contains("df name 2 dl name 2"));
        assertTrue(response.contains("Leeds"));
        assertTrue(response.contains("0113 389 6133"));

    }

    private void assertAddress(String response) {
        assertTrue(response.contains("cf name 2 cl name 2"));
        assertTrue(response.contains("addressline1"));
        assertTrue(response.contains("addressline2"));
        assertTrue(response.contains("addressline3"));
        assertTrue(response.contains("posttown"));
        assertTrue(response.contains("postCode"));
        assertTrue(response.contains("county"));
        assertTrue(response.contains("country"));
    }

    private void validatePostSuccess(String jsonFileName, String path) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .then().assertThat().statusCode(200);
    }

    private String generateDocument(String jsonFileName, String path, int placeholder) {

        Response jsonResponse = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.notificationsGenerated["
                + placeholder
                + "].value.DocumentLink.document_binary_url");
        String response = utils.downloadPdfAndParseToString(documentUrl);
        response = response.replace("\n", "").replace("\r", "");
        return response;
    }

    private String validatePostSuccessReturnPayload(String jsonFileName, String path) {

        Response jsonResponse = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

}
