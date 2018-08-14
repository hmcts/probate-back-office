package uk.gov.hmcts.probate.functional.documents;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static org.junit.Assert.assertTrue;

@RunWith(SerenityRunner.class)
public class SolBaCcdServiceDocumentsTests extends IntegrationTestBase {

    private static final String SOLICITOR_INFO ="Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA";
    private static final String PA = "Extracted personally";
    private static final String PRIMARY_APPLICANT = "EXECUTOR NAME 1 EXECUTOR LAST NAME 1";
    private static final String WILL_MESSAGE ="With A Codicil";
    private static final String ADMIN_MESSAGE = "admin clause limitation message";
    private static final String LIMITATION_MESSAGE = "limitation message";
    private static final String EXECUTOR_LIMITATION_MESSAGE = "executor limitation message";
    private static final String POWER_RESERVED = "Power reserved to other Executors";
    private static final String POWER_RESERVED_SINGLE = "Power reserved to another Executor";
    private static final String TITLE = "CAPTAIN";
    private static final String HONOURS = "OBE";
    private static final String ADD_EXEC_ONE = "and ADD EX FIRST NAME 1 ADD EX LAST NAME 1";
    private static final String ADD_EXEC_TWO = "and ADD EX FIRST NAME 2 ADD EX LAST NAME 2";
    private static final String DOD = "1st January 2000";
    private static final String IHT_NET = "8,000";
    private static final String IHT_GROSS = "10,000";

    @Test
    public void verifySolicitorGenerateGrantShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifySolicitorGenerateGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/document/generate-grant-draft");
    }

    @Test
    public void verifyPersonalApplicantGenerateGrantShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifyPersonalApplicantGenerateGrantDraftShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/document/generate-grant");
    }

    private void validatePostSuccess(String jsonFileName, String path) {
        SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeaders())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path)
                .then().assertThat().statusCode(200);
    }

    private String generateDocument(String jsonFileName, String path) {

        Response jsonResponse = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(path).andReturn();

        JsonPath jsonPath = JsonPath.from(jsonResponse.getBody().asString());
        String documentUrl = jsonPath.get("data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url");

        Response grant = SerenityRest.given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .when().get(documentUrl).andReturn();

        return utils.parsePDFToString(grant.getBody().asInputStream());
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorSols() {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant");

        assertTrue(response.contains(SOLICITOR_INFO));
        assertTrue(response.contains(PRIMARY_APPLICANT));

        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorPA() {
        String response = generateDocument("personalPayloadNotifications.json", "/document/generate-grant");

        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", "/document/generate-grant");

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedMultipleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", "/document/generate-grant");

        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedSingleSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", "/document/generate-grant");

        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithGrantInfoSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", "/document/generate-grant");

        assertTrue(response.contains(WILL_MESSAGE));
        assertTrue(response.contains(ADMIN_MESSAGE));
        assertTrue(response.contains(LIMITATION_MESSAGE));
        assertTrue(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(response.contains(SOLICITOR_INFO));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(TITLE));
        assertTrue(response.contains(HONOURS));

        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(PA));

    }
    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorSols() {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains(SOLICITOR_INFO));
        assertTrue(response.contains(PRIMARY_APPLICANT));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorPA() {
        String response = generateDocument("personalPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains(PA));
        assertTrue(response.contains(PRIMARY_APPLICANT));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", "/document/generate-grant-draft");

        assertTrue(response.contains(ADD_EXEC_ONE));
        assertTrue(response.contains(ADD_EXEC_TWO));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedMultipleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", "/document/generate-grant-draft");

        assertTrue(response.contains(POWER_RESERVED));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedSingleSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", "/document/generate-grant-draft");

        assertTrue(response.contains(POWER_RESERVED_SINGLE));
        assertTrue(response.contains(SOLICITOR_INFO));

        assertTrue(!response.contains(WILL_MESSAGE));
        assertTrue(!response.contains(ADMIN_MESSAGE));
        assertTrue(!response.contains(LIMITATION_MESSAGE));
        assertTrue(!response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(!response.contains(PA));
        assertTrue(!response.contains(TITLE));
        assertTrue(!response.contains(HONOURS));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithGrantInfoSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", "/document/generate-grant-draft");

        assertTrue(response.contains(WILL_MESSAGE));
        assertTrue(response.contains(ADMIN_MESSAGE));
        assertTrue(response.contains(LIMITATION_MESSAGE));
        assertTrue(response.contains(EXECUTOR_LIMITATION_MESSAGE));
        assertTrue(response.contains(SOLICITOR_INFO));
        assertTrue(response.contains(PRIMARY_APPLICANT));
        assertTrue(response.contains(TITLE));
        assertTrue(response.contains(HONOURS));

        assertTrue(!response.contains(POWER_RESERVED));
        assertTrue(!response.contains(POWER_RESERVED_SINGLE));
        assertTrue(!response.contains(PA));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftDateFormat()  {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains(DOD));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDateFormat()  {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant");

        assertTrue(response.contains(DOD));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftMoneyFormat()  {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));

    }

    @Test
    public void verifySuccessForGetDigitalGrantMoneyFormat()  {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant");

        assertTrue(response.contains(IHT_GROSS));
        assertTrue(response.contains(IHT_NET));

    }



}
