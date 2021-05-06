package uk.gov.hmcts.probate.functional.nextsteps;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceNextStepsTests extends IntegrationTestBase {

    @Test
    public void verifyAllDataInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("success.nextsteps.json", "deceasedFirstName", "deceasedLastName",
            "01/01/2018", "refCYA2", "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "TestSOTJobTitle", 
            "firmpc", "a photocopy of the signed legal statement and declaration");
    }

    @Test
    public void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatement() {
        String fullResponse = validatePostRequestSuccessForLegalStatement("success.nextsteps-LegalStatementUploaded"
                + ".json", "deceasedFirstName", "deceasedLastName", "01/01/2018", "refCYA2",
            "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "TestSOTJobTitle", "firmpc");
        assertFalse(fullResponse.contains("a photocopy of the signed legal statement and declaration"));
    }

    @Test
    public void verifyEmptyDeceasedFirstNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedForenames\": \"deceasedFirstName\"",
            "\"deceasedForenames\": \"\"", "caseDetails.data.deceasedForenames");
    }

    @Test
    public void verifyEmptyDeceasedSurNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedSurname\": \"deceasedLastName\"",
            "\"deceasedSurname\": \"\"", "caseDetails.data.deceasedSurname");
    }

    @Test
    public void verifyEmptySolicitorFirmNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSolicitorFirmName\": \"SolicitorFirmName\"",
            "\"solsSolicitorFirmName\": \"\"", "caseDetails.data.solsSolicitorFirmName");
    }

    @Test
    public void verifyEmptySolicitorIHTFormIdReturnsError() {
        validatePostRequestFailureForLegalStatement("\"ihtFormId\": \"IHT205\"", "\"ihtFormId\": \"\"",
            "caseDetails.data.ihtFormId");
    }

    @Test
    public void verifyEmptySolicitorSOTForenamesReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTForenames\": \"Solicitor_fn\"",
            "\"solsSOTForenames\": \"\"", "caseDetails.data.solsSOTForenames");
    }

    @Test
    public void verifyEmptySolicitorSOTSurnameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTSurname\": \"Solicitor_ln\"", "\"solsSOTSurname\": \"\"",
            "caseDetails.data.solsSOTSurname");
    }

    @Test
    public void verifyEmptySolicitorSOTJobTitleameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTJobTitle\": \"TestSOTJobTitle\"",
            "\"solsSOTJobTitle\": \"\"", "caseDetails.data.solsSOTJobTitle");
    }

    @Test
    public void verifyEmptySolicitorFirmAddressLine1ReturnsError() {
        verifyAll("/nextsteps/validate", "failure.missingSolicitorAddressLine1.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.addressLine1");
    }

    @Test
    public void verifyEmptySolicitorFirmPostcodeReturnsError() {
        verifyAll("/nextsteps/validate", "failure.missingSolicitorPostcode.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.postCode");
    }

    private String validatePostRequestSuccessForLegalStatement(String file, String... validationString) {
        Response response = given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(utils.getJsonFromFile(file))
            .post("/nextsteps/confirmation");

        assertEquals(200, response.getStatusCode());
        String responseString = response.getBody().asString();
        for (String val : validationString) {
            assertTrue(responseString.contains(val));
        }
        return responseString;
    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString,
                                                             String errorMsg) {
        Response response = given().relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(replaceString(oldString, replacingString))
            .post("/nextsteps/validate");
        assertEquals(400, response.getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), "Invalid payload");
        assertTrue(response.getBody().asString().contains(errorMsg));
    }

    private String replaceString(String oldJson, String newJson) {
        return utils.getJsonFromFile("success.nextsteps.json").replace(oldJson, newJson);
    }

    private void verifyAll(String url, String jsonInput, int statusCode, String message, String fieldError) {
        Response response = given().relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(utils.getJsonFromFile(jsonInput))
            .post(url);
        assertEquals(statusCode, response.getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), message);
        assertTrue(response.getBody().asString().contains(fieldError));
    }
}
