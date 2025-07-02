package uk.gov.hmcts.probate.functional.caveats;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

public class SolsBoCaveatsServiceTests extends IntegrationTestBase {

    private static final String CAVEAT_RAISED = "/caveat/raise";

    private static final String RAISED_CAVEAT_VALIDATE = "/caveat/raise-caveat-validate";
    private static final String CAVEAT_DEFAULT_VALUES = "/caveat/defaultValues";
    private static final String CAVEAT_GENERAL_MESSAGE = "/caveat/general-message";
    private static final String CAVEAT_CONFIRMATION = "/caveat/confirmation";
    private static final String CAVEAT_EXTEND = "/caveat/extend";
    private static final String CAVEAT_SOLICITOR_CREATE = "/caveat/solsCreate";
    private static final String CAVEAT_SOLICITOR_CREATED = "/caveat/sols-created";
    private static final String CAVEAT_SOLICITOR_UPDATE = "/caveat/solsUpdate";
    private static final String CAVEAT_VALIDATE = "/caveat/sols-complete-application";
    private static final String CAVEAT_VALIDATE_EXTEND = "/caveat/validate-extend";
    private static final String CAVEAT_WITHDRAW = "/caveat/withdraw";
    private static final String DEFAULT_PAYLOAD = "caveatPayloadNotifications.json";
    private static final String DEFAULT_PAYLOAD_WELSH = "caveatPayloadNotificationsWelsh.json";
    private static final String DEFAULT_PAYLOAD_RESPONSE_WELSH = "caveatPayloadNotificationsWelshResponse.txt";
    private static final String PAYLOAD_CAVEAT_NO_DOB = "caveatPayloadNoDOB.json";
    private static final String RESPONSE_CAVEAT_NO_DOB = "caveatPayloadNoDOBResponse.txt";
    private static final String PAYLOAD_CAVEAT_NO_DOB_WELSH = "caveatPayloadNoDOBWelsh.json";
    private static final String RESPONSE_CAVEAT_NO_DOB_WELSH = "caveatPayloadNoDOBWelshResponse.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR = "caveatPayloadNotificationsSolicitor.json";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_WITHDRAWN_RESPONSE =
        "solicitorCaveatWithdrawnExpectedEmailText.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_WITHDRAWN_RESPONSE_WELSH =
        "solicitorCaveatWithdrawnExpectedTextWelsh.txt";
    private static final String DEFAULT_PAYLOAD_SOLICITOR_WELSH = "caveatPayloadNotificationsSolicitorWelsh.json";
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
    private static final String CAVEAT_SOLICITOR_VALIDATE_PAYLOAD_NO_DOB = "/caveat/caveatSolicitorValidateNoDOB.json";
    private static final String CAVEAT_VALIDATE_EXTEND_PAYLOAD = "/caveat/caveatValidateExtend.json";
    private static final String CAVEAT_CASE_WITHDRAW_PAYLOAD = "/caveat/caveatCaseWithdraw.json";

    private static final String RAISE_CAVEAT_VALIDATE_FUTURE_DOD = "/caveat/raiseCaveatCaseValidateFutureDOD.json";

    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String EXPIRY_DATE_KEY = "EXPIRY_DATE_KEY";
    private static final String EXPIRY_DATE_WELSH_KEY = "EXPIRY_DATE_WELSH_KEY";
    private static final String EMAIL_NOTIFICATION_URL =
        "data.notificationsGenerated[0].value.DocumentLink.document_binary_url";

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyCaveatRaisedShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_RAISED);
    }

    @Test
    void verifyRaisedCaveatValidateShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PAYLOAD, RAISED_CAVEAT_VALIDATE);
    }

    @Test
    void verifyRequestWithDobNullReturnsError() throws IOException {
        validatePostFailure(RAISE_CAVEAT_VALIDATE_FUTURE_DOD, "Date of death cannot be in the future",
                200, RAISED_CAVEAT_VALIDATE);
    }

    @Test
    void verifyCaveatDefaultValuesShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_DEFAULT_VALUES);
    }

    @Test
    void verifyCaveatGeneralMessageShouldReturnOkResponseCode() throws IOException {
        validatePostSuccess(DEFAULT_PAYLOAD, CAVEAT_GENERAL_MESSAGE);
    }


    @Test
    void verifySuccessForCaveatDefaultValuesWithEmail() throws IOException {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);

        final JsonPath jsonPath = JsonPath.from(response);
        final String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        final String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(YES, emailRequested);
        assertEquals(NO, bulkPrintRequested);
    }

    @Test
    void verifySuccessForCaveatDefaultValuesWithPaperForm() throws IOException {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD, CAVEAT_RAISED);

        final JsonPath jsonPath = JsonPath.from(response);
        final String paperForm = jsonPath.get("data.paperForm");

        assertEquals(YES, paperForm);
    }


    @Test
    void verifySuccessForCaveatDefaultValuesWithoutEmail() throws IOException {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_DEFAULT_VALUES);

        final JsonPath jsonPath = JsonPath.from(response);
        final String emailRequested = jsonPath.get("data.caveatRaisedEmailNotificationRequested");
        final String bulkPrintRequested = jsonPath.get("data.sendToBulkPrintRequested");

        assertEquals(NO, emailRequested);
        assertEquals(YES, bulkPrintRequested);
    }

    @Test
    void verifyPersonalCaveatRaisedEmailContentsNoDOB() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(PAYLOAD_CAVEAT_NO_DOB, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(RESPONSE_CAVEAT_NO_DOB, EMAIL_NOTIFICATION_URL, responseBody,
            replacements);
    }

    @Test
    void verifyPersonalCaveatRaisedEmailContentsWelsh() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        replacements.put(EXPIRY_DATE_WELSH_KEY, utils.convertToWelsh(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_RESPONSE_WELSH, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    void verifySolicitorCaveatWithdrawnEmailTextEnglish() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR, CAVEAT_WITHDRAW);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_WITHDRAWN_RESPONSE,
            EMAIL_NOTIFICATION_URL, responseBody, replacements);
    }

    @Test
    void verifySolicitorCaveatWithdrawnEmailTextWelsh() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_WELSH, CAVEAT_WITHDRAW);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_WITHDRAWN_RESPONSE_WELSH,
            EMAIL_NOTIFICATION_URL, responseBody, replacements);
    }

    @Test
    void verifyPersonalCaveatRaisedEmailContentsNoDOBWelsh() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(PAYLOAD_CAVEAT_NO_DOB_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        replacements.put(EXPIRY_DATE_WELSH_KEY, utils.convertToWelsh(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsWithExpectedReplacement(RESPONSE_CAVEAT_NO_DOB_WELSH, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    void verifyCaveatRaisedSolicitorPaperEmailContentsNoDOBWelsh() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_NO_DOB_WELSH, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsRegexWithExpectedReplacement(DEFAULT_PAYLOAD_SOLICITOR_RESPONSE_NO_DOB_WELSH,
            EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    void verifyCaveatRaisedSolicitorPaperEmailContentsNoDOB() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(DEFAULT_PAYLOAD_SOLICITOR_NO_DOB, CAVEAT_RAISED);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put(EXPIRY_DATE_KEY, utils.formatDate(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)));
        assertExpectedContentsRegexWithExpectedReplacement(RESPONSE_PAYLOAD_SOLICITOR_NO_DOB, EMAIL_NOTIFICATION_URL,
            responseBody, replacements);
    }

    @Test
    void verifySolicitorCaveatValidateEmailContentsNoDOB() throws IOException {
        assertNotNull(validatePostSuccess(CAVEAT_SOLICITOR_VALIDATE_PAYLOAD_NO_DOB, CAVEAT_VALIDATE));
    }

    @Test
    void verifyCaveatRaisedGeneratesExpiryDateWithoutCaveatorEmailAddress() throws IOException {
        final String response = validatePostSuccessReturnPayload(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED);
        assertTrue(response.contains("\"expiryDate\":\"" + LocalDate.now().plusMonths(CAVEAT_LIFESPAN) + "\""));
    }

    @Test
    void verifySuccessForCaveatRaisedDocumentAndCoversheet() throws IOException {
        final String coversheet = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 0);
        final String response = generateDocument(DEFAULT_PAYLOAD_NO_EMAIL, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertAddress(coversheet);

        assertTrue(!response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));
    }

    @Test
    void verifySuccessForCaveatRaisedDocumentAndCoversheetCTSC() throws IOException {
        final String coversheet = generateDocument(DEFAULT_PAYLOAD_CTSC_NO_EMAIL, CAVEAT_RAISED, 0);
        final String response = generateDocument(DEFAULT_PAYLOAD_CTSC_NO_EMAIL, CAVEAT_RAISED, 1);

        assertCommons(response);
        assertTrue(response.contains("#1542-2740-9293-2452"));
        assertTrue(response.contains("send an email to contactprobate@justice.gov.uk headed Caveat Withdraw"));
        assertAddress(coversheet);
    }

    @Test
    void verifyCaveatConfirmationShouldReturnOKResponseCode() throws IOException {
        final ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON, CAVEAT_CONFIRMATION);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        final String confirmationText = jsonPath.get("confirmation_body");

        assertThat(confirmationText, containsString("Your application has been submitted but cannot be "
                + "processed until payment has been made"));
        assertThat(confirmationText, containsString("**Application reference:** REF1123"));
        assertThat(confirmationText, containsString("using Payment by Account (PBA) or a credit or debit card "
                + "by returning to the case details and selecting the Service Request tab."));
        assertThat(confirmationText, containsString("After you’ve paid, you may need to refresh the page or "
                + "re-enter the case for the payment status to update."));
    }

    @Test
    void verifyCaveatConfirmationShouldReturnBadResponseCode() throws IOException {
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
    void verifyCaveatExtendShouldReturnOKResponseCode() throws IOException {
        final ResponseBody response = validatePostSuccess(CAVEAT_EXTEND_PAYLOAD, CAVEAT_EXTEND);
        JsonPath jsonPath = JsonPath.from(response.asString());

        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"), is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"), containsString("sentEmail"));
    }

    @Test
    void verifyCaveatExtendShouldReturnValidationError() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString
            .replace("\"caveatorEmailAddress\": \"caveator@probate-test.com\",", "\"caveatorEmailAddress\": \"\",");
        final Response response = postJson(jsonAsString, CAVEAT_EXTEND);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("errors[0]"),
            is(equalTo("There is no email address for this caveator. Add an email address or contact them by "
                    + "post.")));
        assertThat(jsonPath.get("errors[1]"),
                is(equalTo("Nid oes cyfeiriad e-bost ar gyfer yr cafeatydd hwn. Ychwanegwch gyfeiriad e-bost "
                        + "neu cysylltwch â nhw drwy'r post.")));
    }

    @Test
    void verifyCaveatSolicitorCreateReturnOkResponseCode() throws IOException {
        String json = utils.getJsonFromFile(CAVEAT_SOLICITOR_CREATE_PAYLOAD);
        final ResponseBody response = validatePostSuccessForPayload(json, CAVEAT_SOLICITOR_CREATED,
                utils.getHeadersWithSolicitorUser());
        final JsonPath jsonPath = JsonPath.from(response.asString());
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertNotNull(jsonPath.get("data.applicantOrganisationPolicy"));
        assertNotNull(jsonPath.get("data.applicantOrganisationPolicy.Organisation.OrganisationID"));
        assertEquals("Probate Test Org",
            jsonPath.get("data.applicantOrganisationPolicy.Organisation.OrganisationName"));
        assertEquals("[APPLICANTSOLICITOR]",
            jsonPath.get("data.applicantOrganisationPolicy.OrgPolicyCaseAssignedRole"));
    }

    @Test
    void verifyCaveatSolicitorCreateReturnsDefaultLocationAndApplicationType() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_CREATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor", "Personal");
        jsonAsString.replaceFirst("ctsc", "Leeds");
        final Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_CREATE);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    void verifyCaveatSolicitorUpdateReturnOKResponseCode() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_SOLICITOR_UPDATE_PAYLOAD, CAVEAT_SOLICITOR_UPDATE);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.paperForm"), is(equalTo("No")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    void verifyCaveatSolicitorUpdateReturnDefaultApplicationTypeAndLocationInResponse() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_SOLICITOR_UPDATE_PAYLOAD);
        jsonAsString.replaceFirst("Solicitor", "Personal");
        jsonAsString.replaceFirst("ctsc", "Leeds");

        final Response response = postJson(jsonAsString, CAVEAT_SOLICITOR_UPDATE);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data.applicationType"), is(equalTo("Solicitor")));
        assertThat(jsonPath.get("data.registryLocation"), is(equalTo("ctsc")));
        assertThat(jsonPath.get("data.errors"), is(nullValue()));
    }

    @Test
    void verifyCaveatValidateShouldReturnOKResponseCode() throws IOException {
        final ResponseBody response = validatePostSuccess(CAVEAT_CASE_CONFIRMATION_JSON_2, CAVEAT_VALIDATE);
        final JsonPath jsonPath = JsonPath.from(response.asString());
        assertNull(jsonPath.get("data.applicationSubmittedDate"));
        assertNull(jsonPath.get("data.expiryDate"));
    }

    @Test
    void verifyCaveatValidateShouldReturnBadResponseCode() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_CASE_CONFIRMATION_JSON);
        jsonAsString = jsonAsString.replace("caveator@probate-test.com", "");

        final Response response = postJson(jsonAsString, CAVEAT_VALIDATE);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(400);
        assertThat(jsonPath.get("message"), is(equalTo("Invalid payload")));
        assertThat(jsonPath.get("fieldErrors[0].field"), is(equalTo("caseDetails.data.caveatorEmailAddress")));
        assertThat(jsonPath.get("fieldErrors[0].code"), is(equalTo("NotBlank")));
    }

    @Test
    void verifyCaveatValidateExtendShouldReturnOKResponseCode() throws IOException {
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
    void verifyCaveatValidateExtendShouldReturnValidationError() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_VALIDATE_EXTEND_PAYLOAD);
        jsonAsString = jsonAsString.replace("endDate", "1900-01-01");

        final Response response = postJson(jsonAsString, CAVEAT_VALIDATE_EXTEND);
        final JsonPath jsonPath = JsonPath.from(response.asString());

        response.then().assertThat().statusCode(200);
        assertThat(jsonPath.get("data"), is(nullValue()));
        assertThat(jsonPath.get("errors[0]"), is(equalTo("Cannot extend an already expired caveat.")));
        assertThat(jsonPath.get("errors[1]"), is(equalTo("Ni ellir ymestyn cafeat sydd eisoes wedi dod i "
                + "ben.")));
    }

    @Test
    void verifyCaveatWithdrawShouldReturnOKResponseCode() throws IOException {
        final ResponseBody responseBody = validatePostSuccess(CAVEAT_CASE_WITHDRAW_PAYLOAD, CAVEAT_WITHDRAW);
        final JsonPath jsonPath = JsonPath.from(responseBody.asString());

        assertThat(jsonPath.get("data.errors"), is(nullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentLink.document_url"), is(notNullValue()));
        assertThat(jsonPath.get("data.notificationsGenerated[0].value.DocumentType"), containsString("sentEmail"));
    }

    @Test
    void verifyCaveatWithdrawWithoutEmailShouldReturnOkResponseCode() throws IOException {
        String jsonAsString = getJsonFromFile(CAVEAT_CASE_WITHDRAW_PAYLOAD);
        jsonAsString = jsonAsString.replaceFirst("\"caveatRaisedEmailNotificationRequested\": \"Yes\",",
            "\"caveatRaisedEmailNotificationRequested\": \"No\",");

        final Response response = postJson(jsonAsString, CAVEAT_WITHDRAW);
        final JsonPath jsonPath = JsonPath.from(response.asString());
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

    private String generateDocument(String jsonFileName, String path, int placeholder) throws IOException {
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

    private String validatePostSuccessReturnPayload(String jsonFileName, String path) throws IOException {
        final Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

    private void validatePostFailure(String jsonFileName, String errorMessage, Integer statusCode, String url)
            throws IOException {
        final String payload = utils.getJsonFromFile(jsonFileName);
        validatePostFailureWithPayload(payload, errorMessage, statusCode, url);
    }

    private void validatePostFailureWithPayload(String payload, String errorMessage, Integer statusCode, String url) {
        final Response response = RestAssured.given()
                .config(config)
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(payload)
                .when().post(url)
                .thenReturn();

        if (statusCode == 200) {
            response.then().assertThat().statusCode(statusCode)
                    .and().body("errors", hasSize(greaterThanOrEqualTo(1)))
                    .and().body("errors", hasItem(Matchers.containsString(errorMessage)));
        } else if (statusCode == 400) {
            response.then().assertThat().statusCode(statusCode)
                    .and().body("error", Matchers.equalTo("Invalid Request"))
                    .and().body("fieldErrors", hasSize(greaterThanOrEqualTo(1)))
                    .and().body("fieldErrors[0].message", Matchers.equalTo(errorMessage));
        } else {
            assert false;
        }
    }
}
