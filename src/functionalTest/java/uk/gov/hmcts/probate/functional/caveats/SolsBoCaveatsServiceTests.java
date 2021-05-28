package uk.gov.hmcts.probate.functional.caveats;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

public class SolsBoCaveatsServiceTests extends IntegrationTestBase {

    private static final String CAVEAT_RAISED = "/caveat/raise";
    private static final String CAVEAT_DEFAULT_VALUES = "/caveat/defaultValues";
    private static final String CAVEAT_GENERAL_MESSAGE = "/caveat/general-message";
    private static final String CAVEAT_CONFIRMATION = "/caveat/confirmation";
    private static final String CAVEAT_EXTEND = "/caveat/extend";
    private static final String CAVEAT_SOLICITOR_CREATE = "/caveat/solsCreate";
    private static final String CAVEAT_SOLICITOR_UPDATE = "/caveat/solsUpdate";
    private static final String CAVEAT_VALIDATE = "/caveat/validate";
    private static final String CAVEAT_VALIDATE_EXTEND = "/caveat/validate-extend";
    private static final String CAVEAT_WITHDRAW = "/caveat/withdraw";
    private static final String DEFAULT_PAYLOAD = "caveatPayloadNotifications.json";
    private static final String DEFAULT_PAYLOAD_RESPONSE = "caveatPayloadNotificationsResponse.txt";
    private static final String DEFAULT_PAYLOAD_WELSH = "caveatPayloadNotificationsWelsh.json";
    private static final String DEFAULT_PAYLOAD_RESPONSE_WELSH = "caveatPayloadNotificationsWelshResponse.txt";
    private static final String DEFAULT_PAYLOAD_CTSC = "caveatPayloadNotificationsCTSC.json";
    private static final String DEFAULT_PAYLOAD_CTSC_RESPONSE = "caveatPayloadNotificationsCTSCResponse.txt";
    private static final String DEFAULT_PAYLOAD_CTSC_NO_DOB = "caveatPayloadNotificationsCTSCNoDOB.json";
    private static final String DEFAULT_PAYLOAD_CTSC_NO_DOB_RESPONSE = "caveatPayloadNotificationsCTSCNoDOBResponse" 
        + ".txt";
    private static final String PAYLOAD_CAVEAT_NO_DOB = "caveatPayloadNoDOB.json";
    private static final String RESPONSE_CAVEAT_NO_DOB = "caveatPayloadNoDOBResponse.txt";
    private static final String PAYLOAD_CAVEAT_NO_DOB_WELSH = "caveatPayloadNoDOBWelsh.json";
    private static final String RESPONSE_CAVEAT_NO_DOB_WELSH = "caveatPayloadNoDOBWelshResponse.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR = "caveatPayloadNotificationsSolicitor.json";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_RESPONSE = "caveatPayloadNotificationsSolicitorResponse.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_WELSH = "caveatPayloadNotificationsSolicitorWelsh.json";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_RESPONSE_WELSH = 
        "caveatPayloadNotificationsSolicitorResponseWelsh.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_NO_DOB = "caveatPayloadNotificationsSolicitorNoDOB.json";
    private static final String RESPONSE_PAYLOAD_SOLICITOR_NO_DOB = "caveatPayloadNotificationsSolicitorNoDOBResponse" 
        + ".txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_NO_DOB_WELSH =
        "caveatPayloadNotificationsSolicitorNoDOBWelsh.json";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_RESPONSE_NO_DOB_WELSH =
        "caveatPayloadNotificationsSolicitorNoDOBWelshResponse.txt";
    private static final String DEFAULT_PAYLOAD_NO_EMAIL = "caveatPayloadNotificationsNoEmail.json";
    private static final String DEFAULT_PAYLOAD_CTSC_NO_EMAIL = "caveatPayloadNotificationsNoEmailCTSC.json";
    private static final String CAVEAT_CASE_CONFIRMATION_JSON = "/caveat/caveatCaseConfirmation.json";
    private static final String CAVEAT_CASE_CONFIRMATION_JSON_2 = "/caveat/caveatCaseConfirmation2.json";
    private static final String CAVEAT_EXTEND_PAYLOAD = "/caveat/caveatExtendPayloadExtend.json";
    private static final String CAVEAT_SOLICITOR_CREATE_PAYLOAD = "/caveat/caveatSolicitorCreate.json";
    private static final String CAVEAT_SOLICITOR_UPDATE_PAYLOAD = "/caveat/caveatSolicitorUpdate.json";
    private static final String CAVEAT_SOLICITOR_VALIDATE_PAYLOAD = "/caveat/caveatSolicitorValidate.json";
    private static final String CAVEAT_SOLICITOR_VALIDATE_RESPONSE = "caveatSolicitorValidateResponse.txt";
    private static final String CAVEAT_SOLICITOR_VALIDATE_PAYLOAD_NO_DOB = "/caveat/caveatSolicitorValidateNoDOB.json";
    private static final String CAVEAT_SOLICITOR_VALIDATE_RESPONSE_NO_DOB = "caveatSolicitorValidateResponseNoDOB.txt";
    private static final String CAVEAT_VALIDATE_EXTEND_PAYLOAD = "/caveat/caveatValidateExtend.json";
    private static final String CAVEAT_CASE_WITHDRAW_PAYLOAD = "/caveat/caveatCaseWithdraw.json";
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String EXPIRY_DATE_KEY = "EXPIRY_DATE_KEY";
    private static final String EXPIRY_DATE_WELSH_KEY = "EXPIRY_DATE_WELSH_KEY";
    private static final String EMAIL_NOTIFICATION_URL =
        "data.notificationsGenerated[0].value.DocumentLink.document_binary_url";

    @Before
    public void setUp() {
        initialiseConfig();
    }

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
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);

        final JsonPath jsonPath = JsonPath.from(response);
        final String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        final String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(YES, emailRequested);
        assertEquals(NO, bulkPrintRequested);
    }

    @Test
    public void verifySuccessForCaveatDefaultValuesWithPaperForm() {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);

        final JsonPath jsonPath = JsonPath.from(response);
        final String paperForm = jsonPath.get("data.paperForm");

        assertEquals(YES, paperForm);
    }


    @Test
    public void verifySuccessForCaveatDefaultValuesWithoutEmail() {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_DEFAULT_VALUES);

        final JsonPath jsonPath = JsonPath.from(response);
        final String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        final String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(NO, emailRequested);
        assertEquals(YES, bulkPrintRequested);
    }

    @Test
    public void verifyPersonalCaveatRaisedEmailContents() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_RESPONSE, EMAIL_NOTIFICATION_URL, responseBody,
            replacements);
    }

    @Test
    public void verifyPersonalCaveatRaisedEmailContentsNoDOB() {
        final ResponseBody responseBody = validatePostSuccess(PAYLOAD_CAVEAT_NO_DOB, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(RESPONSE_CAVEAT_NO_DOB, EMAIL_NOTIFICATION_URL, responseBody,
            replacements);
    }

    @Test
    public void verifyPersonalCaveatRaisedEmailContentsWelsh() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        replacements.put(EXPIRY_DATE_WELSH_KEY, utils.convertToWelsh(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_RESPONSE_WELSH, EMAIL_NOTIFICATION_URL, 
            responseBody, replacements);
    }

    @Test
    public void verifyPersonalCaveatRaisedEmailContentsNoDOBWelsh() {
        final ResponseBody responseBody = validatePostSuccess(PAYLOAD_CAVEAT_NO_DOB_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        replacements.put(EXPIRY_DATE_WELSH_KEY, utils.convertToWelsh(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(RESPONSE_CAVEAT_NO_DOB_WELSH, EMAIL_NOTIFICATION_URL, 
            responseBody, replacements);
    }

    @Test
    public void verifyPersonalCaveatRaisedCtscEmailContents() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_CTSC, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_CTSC_RESPONSE, EMAIL_NOTIFICATION_URL,
            responseBody,
            replacements);
    }

    @Test
    public void verifyPersonalCaveatRaisedCtscEmailContentsNoDOB() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_CTSC_NO_DOB, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_CTSC_NO_DOB_RESPONSE, EMAIL_NOTIFICATION_URL,
            responseBody,
            replacements);
    }

    @Test
    public void verifyCaveatRaisedSolicitorPaperEmailContents() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_RESPONSE, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    public void verifyCaveatRaisedSolicitorPaperEmailContentsWelsh() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_RESPONSE_WELSH, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    public void verifyCaveatRaisedSolicitorPaperEmailContentsNoDOBWelsh() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_NO_DOB_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_RESPONSE_NO_DOB_WELSH, 
            EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    public void verifyCaveatRaisedSolicitorPaperEmailContentsNoDOB() {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_NO_DOB, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(RESPONSE_PAYLOAD_SOLICITOR_NO_DOB, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    public void verifySolicitorCaveatRaisedEmailContents() {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_SOLICITOR_VALIDATE_PAYLOAD, CAVEAT_VALIDATE);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(CAVEAT_SOLICITOR_VALIDATE_RESPONSE, EMAIL_NOTIFICATION_URL,
            responseBody,
            replacements);
    }

    @Test
    public void verifySolicitorCaveatRaisedEmailContentsNoDOB() {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_SOLICITOR_VALIDATE_PAYLOAD_NO_DOB,
                CAVEAT_VALIDATE);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(CAVEAT_SOLICITOR_VALIDATE_RESPONSE_NO_DOB, EMAIL_NOTIFICATION_URL,
            responseBody,
            replacements);
    }

    @Test
    public void verifyCaveatRaisedGeneratesExpiryDateWithoutCaveatorEmailAddress() {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED);
        assertTrue(response.contains("\"expiryDate\":\"" + LocalDate.now().plusMonths(CAVEAT_LIFESPAN) + "\""));
    }

    @Test
    public void verifySuccessForCaveatRaisedDocumentAndCoversheet() {
        final String coversheet = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 0);
        final String response = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertAddress(coversheet);

        assertTrue(!response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));
    }

    @Test
    public void verifySuccessForCaveatRaisedDocumentAndCoversheetCTSC() {
        final String coversheet = generateDocument(DEFAULT_PAYLOAD_CTSC_NO_EMAIL, CAVEAT_RAISED, 0);
        final String response = generateDocument(DEFAULT_PAYLOAD_CTSC_NO_EMAIL, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertTrue(response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));
        assertAddress(coversheet);
    }

    @Test
    public void verifyCaveatConfirmationShouldReturnOKResponseCode() {
        final ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON, CAVEAT_CONFIRMATION);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        final String confirmationText = jsonPath.get("confirmation_body");

        assertThat(confirmationText, containsString("This caveat application has now been submitted"));
        assertThat(confirmationText, containsString("**Your reference:** REF1123"));
        assertThat(confirmationText, containsString("**Application fee** &pound;3.00"));
        assertThat(confirmationText, containsString("**Payment method** fee account"));
        assertThat(confirmationText, containsString("**Selected PBA account** PBA0082126"));
        assertThat(confirmationText, containsString("**Customer reference** appref-PAY1"));
    }

    @Test
    public void verifyCaveatConfirmationShouldReturnBadResponseCode() {
        String jsonAsString = getJsonFromFile(CAVEAT_CASE_CONFIRMATION_JSON);
        jsonAsString = jsonAsString
            .replace("\"caveatorEmailAddress\": \"caveator@probate-test.com\",", "\"caveatorEmailAddress\": \"\",");
        final Response response = postJson(jsonAsString, CAVEAT_CONFIRMATION);
        response.then().assertThat().statusCode(400);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        assertThat(jsonPath.get("message"), is(equalTo("Invalid payload")));
        assertThat(jsonPath.get("fieldErrors[0].field"), is(equalTo("caseDetails.data.caveatorEmailAddress")));
        assertThat(jsonPath.get("fieldErrors[0].code"), is(equalTo("NotBlank")));
    }

    @Test
    public void verifyCaveatExtendShouldReturnOKResponseCode() {
        final ResponseBody response = validatePostSuccess(CAVEAT_EXTEND_PAYLOAD, CAVEAT_EXTEND);
        response.prettyPrint();
        JsonPath jsonPath = JsonPath.from(response.asString());

        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"), is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"), containsString("sentEmail"));
    }

    @Test
    public void verifyCaveatExtendShouldReturnValidationError() {
        String jsonAsString = getJsonFromFile(CAVEAT_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString
            .replace("\"caveatorEmailAddress\": \"caveator@probate-test.com\",", "\"caveatorEmailAddress\": \"\",");
        final Response response = postJson(jsonAsString, CAVEAT_EXTEND);
        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("errors[0]"),
            is(equalTo("There is no email address for this caveator. Add an email address or contact them by post.")));
    }

    @Test
    public void verifyCaveatSolicitorCreateReturnOkResponseCode() {
        final ResponseBody response = validatePostSuccess(CAVEAT_SOLICITOR_CREATE_PAYLOAD, CAVEAT_SOLICITOR_CREATE);
        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorCreateReturnsDefaultLocationAndApplicationType() {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_CREATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor", "Personal");
        jsonAsString.replaceFirst("ctsc", "Leeds");
        final Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_CREATE);
        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorUpdateReturnOKResponseCode() {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_SOLICITOR_UPDATE_PAYLOAD, CAVEAT_SOLICITOR_UPDATE);
        responseBody.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.paperForm"), is(equalTo("No")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatSolicitorUpdateReturnDefaultApplicationTypeAndLocationInResponse() {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_UPDATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor", "Personal");
        jsonAsString.replaceFirst("ctsc", "Leeds");

        final Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_UPDATE);
        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    public void verifyCaveatValidateShouldReturnOKResponseCode() {
        final ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON_2, CAVEAT_VALIDATE);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        final DateTimeFormatter iso8601Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDate today = LocalDate.now();
        final LocalDate extended = today.plusMonths(6);
        today.format(iso8601Formatter);

        assertThat(jsonPath.get("data.applicationSubmittedDate"), is(equalTo(today.format(iso8601Formatter))));
        assertThat(jsonPath.get("data.expiryDate"), is(equalTo(extended.format(iso8601Formatter))));
    }

    @Test
    public void verifyCaveatValidateShouldReturnBadResponseCode() {
        String jsonAsString = getJsonFromFile(CAVEAT_CASE_CONFIRMATION_JSON);
        jsonAsString = jsonAsString.replace("caveator@probate-test.com", "");

        final Response response = postJson(jsonAsString, CAVEAT_VALIDATE);
        response.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(400);
        assertThat(jsonPath.get("message"), is(equalTo("Invalid payload")));
        assertThat(jsonPath.get("fieldErrors[0].field"), is(equalTo("caseDetails.data.caveatorEmailAddress")));
        assertThat(jsonPath.get("fieldErrors[0].code"), is(equalTo("NotBlank")));
    }

    @Test
    public void verifyCaveatValidateExtendShouldReturnOKResponseCode() {
        final DateTimeFormatter iso8601Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDate localDate = LocalDate.now();
        final LocalDate extended = localDate.plusMonths(6);
        final String today = localDate.format(iso8601Formatter);
        final String extendedDate = extended.format(iso8601Formatter);
        String jsonAsString = getJsonFromFile(CAVEAT_VALIDATE_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString.replace("endDate", today);

        final Response response = postJson(jsonAsString, CAVEAT_VALIDATE_EXTEND);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.expiryDate"), is(equalTo(extendedDate)));
    }

    @Test
    public void verifyCaveatValidateExtendShouldReturnValidationError() {
        String jsonAsString = getJsonFromFile(CAVEAT_VALIDATE_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString.replace("endDate", "1900-01-01");

        final Response response = postJson(jsonAsString, CAVEAT_VALIDATE_EXTEND);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data"), is(nullValue()));
        assertThat(jsonPath.get("errors[0]"), is(equalTo("Cannot extend an already expired caveat.")));
    }

    @Test
    public void verifyCaveatWithdrawShouldReturnOKResponseCode() {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_CASE_WITHDRAW_PAYLOAD, CAVEAT_WITHDRAW);
        responseBody.prettyPrint();
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"), is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"), containsString("sentEmail"));
    }

    @Test
    public void verifyCaveatWithdrawWithoutEmailShouldReturnOkResponseCode() {
        String jsonAsString = getJsonFromFile(CAVEAT_CASE_WITHDRAW_PAYLOAD);
        jsonAsString = jsonAsString.replaceFirst("\"caveatRaisedEmailNotificationRequested\": \"Yes\",",
            "\"caveatRaisedEmailNotificationRequested\": \"No\",");

        final Response response = postJson(jsonAsString, CAVEAT_WITHDRAW);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"),
            containsString("caveatCoversheet"));
        assertThat(jsonPath.get("data.notificationsGenerated[1].value.DocumentType"),
            containsString("caveatWithdrawn"));

    }

    private Response postJson(String jsonAsString, String caveatConfirmation) {
        return RestAssured.given()
            .config(config)
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
        final Response jsonResponse = RestAssured.given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        final JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        final String documentUrl = jsonPath.get("data.notificationsGenerated["
            + placeholder
            + "].value.DocumentLink.document_binary_url");
        final String response = utils.downloadPdfAndParseToString(documentUrl);
        return removeCrLfs(response);
    }

    private String validatePostSuccessReturnPayload(String jsonFileName, String path) {
        final Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }
}
