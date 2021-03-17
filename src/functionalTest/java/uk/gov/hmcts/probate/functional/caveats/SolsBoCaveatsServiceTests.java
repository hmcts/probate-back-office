package uk.gov.hmcts.probate.functional.caveats;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Test;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

public class SolsBoCaveatsServiceTests extends IntegrationTestBase {

    private static final String CAVEAT_RAISED = "/caveat/raise";
    private static final String CAVEAT_DEFAULT_VALUES = "/caveat/defaultValues";
    private static final String CAVEAT_GENERAL_MESSAGE= "/caveat/general-message";
    private static final String CAVEAT_CONFIRMATION ="/caveat/confirmation";
    private static final String CAVEAT_EXTEND = "/caveat/extend";
    private static final String CAVEAT_SOLICITOR_CREATE = "/caveat/solsCreate";
    private static final String CAVEAT_SOLICITOR_UPDATE = "/caveat/solsUpdate";
    private static final String CAVEAT_VALIDATE = "/caveat/validate";
    private static final String CAVEAT_VALIDATE_EXTEND = "/caveat/validate-extend";
    private static final String CAVEAT_WITHDRAW = "/caveat/withdraw";
    private static final String DEFAULT_PAYLOAD= "caveatPayloadNotifications.json";
    private static final String DEFAULT_PAYLOAD_NO_EMAIL= "caveatPayloadNotificationsNoEmail.json";
    private static final String DEFAULT_PAYLOAD_CTSC= "caveatPayloadNotificationsNoEmailCTSC.json";
    private static final String CAVEAT_CASE_CONFIRMATION_JSON = "/caveat/caveatCaseConfirmation.json";
    private static final String CAVEAT_EXTEND_PAYLOAD ="/caveat/caveatExtendPayloadExtend.json";
    private static final String CAVEAT_SOLICITOR_CREATE_PAYLOAD = "/caveat/caveatSolicitorCreate.json";
    private static final String CAVEAT_SOLICITOR_UPDATE_PAYLOAD = "/caveat/caveatSolicitorUpdate.json";
    private static final String CAVEAT_VALIDATE_EXTEND_PAYLOAD = "/caveat/caveatValidateExtend.json";
    private static final String CAVEAT_CASE_WITHDRAW_PAYLOAD = "/caveat/caveatCaseWithdraw.json";


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
    public void verifySuccessForCaveatDefaultValuesWithPaperForm() {
        String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);

        JsonPath jsonPath = JsonPath.from(response);
        String paperForm = jsonPath.get("data.paperForm");

        assertEquals(YES, paperForm);

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
        assertTrue(response.contains("caveator@probate-test.com"));

    }

    @Test
    public void verifySuccessForCaveatRaisedEmailApplicationFee() {
        String response = generateDocument(DEFAULT_PAYLOAD, CAVEAT_RAISED, 0);

        assertCommons(response);
        assertTrue(response.contains("1542274092932452"));
        assertTrue(response.contains("£3 fee"));
        assertTrue(response.contains("caveator@probate-test.com"));

    }

    @Test
    public void verifyCaveatRaisedGeneratesExpiryDateWithCaveatorEmailAddress() {
        String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);
        assertTrue(response.contains("\"expiryDate\":\"" + LocalDate.now().plusMonths(CAVEAT_LIFESPAN)+ "\""));

    }

    @Test
    public void verifyCaveatRaisedGeneratesExpiryDateWithoutCaveatorEmailAddress() {
        String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED);
        assertTrue(response.contains("\"expiryDate\":\"" + LocalDate.now().plusMonths(CAVEAT_LIFESPAN) + "\""));
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

    @Test
    public void verifyCaveatConfirmationShouldReturnOKResponseCode() {
        ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON, CAVEAT_CONFIRMATION);
        JsonPath jsonPath = JsonPath.from(response.asString());
        String confirmationText = jsonPath.get("confirmation_body");

        assertThat(confirmationText, containsString("This caveat application has now been submitted"));
    }

    @Test
    public void verifyCaveatConfirmationShouldReturnBadResponseCode() {
        String jsonAsString =  getJsonFromFile(CAVEAT_CASE_CONFIRMATION_JSON);
        jsonAsString =jsonAsString.replace("\"caveatorEmailAddress\": \"caveator@probate-test.com\",","\"caveatorEmailAddress\": \"\",");
        Response response = postJson(jsonAsString, CAVEAT_CONFIRMATION);
        response.then().assertThat().statusCode(400);
        JsonPath jsonPath = JsonPath.from(response.asString());

        assertThat(jsonPath.get("message"),is(equalTo("Invalid payload")));
        assertThat(jsonPath.get("fieldErrors[0].field"),is(equalTo("caseDetails.data.caveatorEmailAddress")));
        assertThat(jsonPath.get("fieldErrors[0].code"),is(equalTo("NotBlank")));
    }

    @Test
    public void verifyCaveatExtendShouldReturnOKResponseCode(){
        ResponseBody response = validatePostSuccess(CAVEAT_EXTEND_PAYLOAD, CAVEAT_EXTEND);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        assertThat(jsonPath.get("data.errors"),is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"),is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"),containsString("sentEmail"));

    }

    @Test
    public void verifyCaveatExtendShouldReturnValidationError(){
        String jsonAsString =  getJsonFromFile(CAVEAT_EXTEND_PAYLOAD);
        jsonAsString =jsonAsString.replace("\"caveatorEmailAddress\": \"caveator@probate-test.com\",","\"caveatorEmailAddress\": \"\",");
        Response response = postJson(jsonAsString, CAVEAT_EXTEND);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("errors[0]"),is(equalTo("There is no email address for this caveator. Add an email address or contact them by post.")));
    }

    @Test
    public void verifyCaveatSolicitorCreateReturnOkResponseCode() {
        ResponseBody response = validatePostSuccess(CAVEAT_SOLICITOR_CREATE_PAYLOAD, CAVEAT_SOLICITOR_CREATE);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertThat(jsonPath.get("data.applicationType"),is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"),is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"),is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorCreateReturnsDefaultLocationAndApplicationType() {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_CREATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor","Personal");
        jsonAsString.replaceFirst("ctsc","Leeds");
        Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_CREATE);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"),is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"),is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"),is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorUpdateReturnOKResponseCode() {
        ResponseBody responseBody = validatePostSuccess(CAVEAT_SOLICITOR_UPDATE_PAYLOAD, CAVEAT_SOLICITOR_UPDATE);
        responseBody.prettyPrint();
        JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.paperForm"), is(equalTo("No")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorUpdateReturnDefaultApplicationTypeAndLocationInResponse() {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_UPDATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor", "Personal");
        jsonAsString.replaceFirst("ctsc", "Leeds");

        Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_UPDATE);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatValidateShouldReturnOKResponseCode() {
        ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON, CAVEAT_VALIDATE);
        JsonPath jsonPath = JsonPath.from(response.asString());
        DateTimeFormatter iso_8601_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate extended = today.plusMonths(6);
        today.format(iso_8601_formatter);

        assertThat(jsonPath.get("data.applicationSubmittedDate"), is(equalTo(today.format(iso_8601_formatter))));
        assertThat(jsonPath.get("data.expiryDate"), is(equalTo(extended.format(iso_8601_formatter))));
    }

    @Test
    public void verifyCaveatValidateShouldReturnBadResponseCode() {
        String jsonAsString =  getJsonFromFile(CAVEAT_CASE_CONFIRMATION_JSON);
        jsonAsString =jsonAsString.replace("caveator@probate-test.com","");

        Response response = postJson(jsonAsString, CAVEAT_VALIDATE);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(400);
        assertThat(jsonPath.get("message"),is(equalTo("Invalid payload")));
        assertThat(jsonPath.get("fieldErrors[0].field"),is(equalTo("caseDetails.data.caveatorEmailAddress")));
        assertThat(jsonPath.get("fieldErrors[0].code"),is(equalTo("NotBlank")));
    }
    @Test
    public void verifyCaveatValidateExtendShouldReturnOKResponseCode(){
        DateTimeFormatter iso_8601_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        LocalDate extended = localDate.plusMonths(6);
        String today = localDate.format(iso_8601_formatter);
        String extendedDate = extended.format(iso_8601_formatter);
        String jsonAsString =  getJsonFromFile(CAVEAT_VALIDATE_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString.replace("endDate",today);

        Response response = postJson(jsonAsString, CAVEAT_VALIDATE_EXTEND);
        JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.expiryDate"), is(equalTo(extendedDate)));
    }

    @Test
    public void verifyCaveatValidateExtendShouldReturnValidationError(){

        String jsonAsString =  getJsonFromFile(CAVEAT_VALIDATE_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString.replace("endDate","1900-01-01");

        Response response = postJson(jsonAsString, CAVEAT_VALIDATE_EXTEND);
        JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data"), is(nullValue()));
        assertThat(jsonPath.get("errors[0]"), is(equalTo("Cannot extend an already expired caveat.")));
    }

    @Test
    public void verifyCaveatWithdrawShouldReturnOKResponseCode() {
        ResponseBody responseBody = validatePostSuccess(CAVEAT_CASE_WITHDRAW_PAYLOAD, CAVEAT_WITHDRAW);
        responseBody.prettyPrint();
        JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.errors"),is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"),is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"),containsString("sentEmail"));
    }

    @Test
    public void verifyCaveatWithdrawWithoutEmailShouldReturnOkResponseCode(){

        String jsonAsString =  getJsonFromFile(CAVEAT_CASE_WITHDRAW_PAYLOAD);
        jsonAsString = jsonAsString.replaceFirst("\"caveatRaisedEmailNotificationRequested\": \"Yes\",","\"caveatRaisedEmailNotificationRequested\": \"No\",");

        Response response = postJson(jsonAsString, CAVEAT_WITHDRAW);
        JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"),containsString("caveatCoversheet"));
        assertThat(jsonPath.get("data.notificationsGenerated[1].value.DocumentType"),containsString("caveatWithdrawn"));

    }
    private Response postJson(String jsonAsString, String caveatConfirmation) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(jsonAsString)
                .when().post(caveatConfirmation)
                .andReturn();
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
        assertTrue(response.contains("postcode"));
        assertTrue(response.contains("county"));
        assertTrue(response.contains("country"));
    }

    private String generateDocument(String jsonFileName, String path, int placeholder) {

        Response jsonResponse = RestAssured.given()
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

        Response jsonResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

}
