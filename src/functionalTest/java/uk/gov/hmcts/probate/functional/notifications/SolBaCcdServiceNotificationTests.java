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

    @Test
    public void verifySolicitorDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/notify/documents-received");
    }

    @Test
    public void verifyPersonalApplicantDocumentsReceivedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/notify/documents-received");
    }

    @Test
    public void verifySolicitorGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("solicitorPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifyPersonalApplicantGrantIssuedShouldReturnOkResponseCode() {
        validatePostSuccess("personalPayloadNotifications.json", "/document/generate-grant");
    }

    @Test
    public void verifySolicitorCaseStoppedShouldReturnOkResponseCode() {

        String document = sendEmail("solicitorPayloadNotifications.json", "/notify/case-stopped");

        assertTrue(document.contains(SOLS_STOP_DETAILS));
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedShouldReturnOkResponseCode() {

        String document = sendEmail("personalPayloadNotifications.json", "/notify/case-stopped");

        assertTrue(document.contains(PA_STOP_DETAILS));
    }

    @Test
    public void verifyPersonalApplicantCaseStoppedContentIsOk() {
        String document = sendEmail("personalPayloadNotifications.json", "/notify/case-stopped");

        assertTrue(document.contains(PA_STOP_DETAILS));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("Executor name 1 Executor Last Name 1"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("2000-01-01"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
    }

    @Test
    public void verifySolicitorCaseStoppedContentIsOkay() {

        String document = sendEmail("solicitorPayloadNotifications.json", "/notify/case-stopped");

        assertTrue(document.contains(SOLS_STOP_DETAILS));
        assertTrue(document.contains("Birmingham"));
        assertTrue(document.contains("1231-3984-3949-0300"));
        assertTrue(document.contains("name"));
        assertTrue(document.contains("1528365719153338"));
        assertTrue(document.contains("2000-01-01"));
        assertTrue(document.contains("Deceased First Name Deceased Last Name"));
    }


    private String sendEmail(String fileName, String url) {
        ResponseBody body = validatePostSuccess(fileName, url);

        JsonPath jsonPath = JsonPath.from(body.asString());
        String documentUrl = jsonPath.get("data.probateDocumentsGenerated[0].value.DocumentLink.document_binary_url");

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
}
