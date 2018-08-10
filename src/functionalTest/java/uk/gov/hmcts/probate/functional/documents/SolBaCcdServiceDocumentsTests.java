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

        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithSingleExecutorPA() {
        String response = generateDocument("personalPayloadNotifications.json", "/document/generate-grant");

        assertTrue(response.contains("Extracted personally"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithMultipleExecutorsSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", "/document/generate-grant");

        assertTrue(response.contains("and ADD EX FIRST NAME 1 ADD EX LAST NAME 1"));
        assertTrue(response.contains("and ADD EX FIRST NAME 2 ADD EX LAST NAME 2"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedMultipleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", "/document/generate-grant");

        assertTrue(response.contains("Power reserved to other Executors"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));
    }

    @Test
    public void verifySuccessForGetDigitalGrantWithPowerReservedSingleSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", "/document/generate-grant");

        assertTrue(response.contains("Power reserved to another Executor"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantWithGrantInfoSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", "/document/generate-grant");

        assertTrue(response.contains("With A Codicil"));
        assertTrue(response.contains("admin clause limitation message"));
        assertTrue(response.contains("limitation message"));
        assertTrue(response.contains("executor limitation message"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));
        assertTrue(response.contains("CAPTAIN"));
        assertTrue(response.contains("OBE"));

        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("Extracted personally"));

    }
    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorSols() {
        String response = generateDocument("solicitorPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithSingleExecutorPA() {
        String response = generateDocument("personalPayloadNotifications.json", "/document/generate-grant-draft");

        assertTrue(response.contains("Extracted personally"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithMultipleExecutorsSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsMultipleExecutors.json", "/document/generate-grant-draft");

        assertTrue(response.contains("and ADD EX FIRST NAME 1 ADD EX LAST NAME 1"));
        assertTrue(response.contains("and ADD EX FIRST NAME 2 ADD EX LAST NAME 2"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedMultipleSOls() {
        String response = generateDocument("solicitorPayloadNotificationsPowerReservedMultiple.json", "/document/generate-grant-draft");

        assertTrue(response.contains("Power reserved to other Executors"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));
    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithPowerReservedSingleSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsPowerReserved.json", "/document/generate-grant-draft");

        assertTrue(response.contains("Power reserved to another Executor"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));

        assertTrue(!response.contains("with a will"));
        assertTrue(!response.contains("admin clause limitation message"));
        assertTrue(!response.contains("limitation message"));
        assertTrue(!response.contains("executor limitation message"));
        assertTrue(!response.contains("Extracted personally"));
        assertTrue(!response.contains("CAPTAIN"));
        assertTrue(!response.contains("OBE"));

    }

    @Test
    public void verifySuccessForGetDigitalGrantDraftWithGrantInfoSOls()  {
        String response = generateDocument("solicitorPayloadNotificationsGrantInfo.json", "/document/generate-grant-draft");

        assertTrue(response.contains("With A Codicil"));
        assertTrue(response.contains("admin clause limitation message"));
        assertTrue(response.contains("limitation message"));
        assertTrue(response.contains("executor limitation message"));
        assertTrue(response.contains("Extracted Solicitor Firm Name (Ref: 1231-3984-3949-0300) KT10 0LA"));
        assertTrue(response.contains("EXECUTOR NAME 1 EXECUTOR LAST NAME 1"));
        assertTrue(response.contains("CAPTAIN"));
        assertTrue(response.contains("OBE"));

        assertTrue(!response.contains("Power reserved to other executors"));
        assertTrue(!response.contains("Power reserved to another executor"));
        assertTrue(!response.contains("Extracted personally"));

    }



}
