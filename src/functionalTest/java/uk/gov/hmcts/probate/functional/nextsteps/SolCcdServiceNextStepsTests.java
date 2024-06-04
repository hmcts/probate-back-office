package uk.gov.hmcts.probate.functional.nextsteps;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SerenityJUnit5Extension.class)
public class SolCcdServiceNextStepsTests extends IntegrationTestBase {
    private static final String VALIDATE_URL = "/nextsteps/validate";

    @Test
    void verifyAllDataInTheReturnedMarkdown() throws IOException {
        validatePostRequestSuccessForLegalStatement("success.nextsteps.json",
            "deceasedFirstName",
            "deceasedLastName", "01/01/2018", "refCYA2", "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln",
            "firmpc", "a photocopy of the signed legal statement and declaration");
    }

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatement() throws IOException {
        String fullResponse = validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded"
                + ".json", "deceasedFirstName", "deceasedLastName", "01/01/2018", "refCYA2",
            "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "firmpc").getBody().asString();
        assertFalse(fullResponse.contains("a photocopy of the signed legal statement and declaration"));
        assertFalse(fullResponse.contains("(PA16)"));
    }

    @Test
    void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatementWithPA16Form() throws IOException {
        String fullResponse = validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded-PA16"
                + ".json", "deceasedFirstName", "deceasedLastName", "01/01/2018", "refCYA2",
            "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "firmpc", "(PA16)").getBody().asString();
        assertFalse(fullResponse.contains("a photocopy of the signed legal statement and declaration"));
    }

    @Test
    void shouldIncludePA14Link() throws IOException {
        final String response = transformCase("solicitorValidateProbateExecutorsPA14.json", VALIDATE_URL);
        assertTrue(response.contains("(PA14)"));
    }

    @Test
    void shouldIncludePA15Link() throws IOException {
        final String response = transformCase("solicitorValidateProbateExecutorsPA15.json", VALIDATE_URL);
        assertTrue(response.contains("(PA15)"));
    }

    @Test
    void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatementWithPA17Form() throws IOException {
        validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded-PA17"
                + ".json",  "(PA17)");
    }

    @Test
    void verifyAllDetailsInTheReturnedMarkdown() throws IOException {
        validatePostRequestSuccessForLegalStatement(Arrays.asList("deceasedFirstName", "deceasedLastName",
            "01/01/2018", "refCYA2", "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln",
            "firmpc", "appref-PAY1"));
    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate() throws IOException {
        String dir = "/exceptedEstates/ihtEstateBeforeSwitchDate/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesNo() throws IOException {
        String dir = "/exceptedEstates/ihtEstateCompletedNo/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesCompletedYes207() throws IOException {
        String dir = "/exceptedEstates/ihtEstateCompletedYes207/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    void verifyGenerateSolsGopExpectedEstatesCompletedYes400421() throws IOException {
        String dir = "/exceptedEstates/ihtEstateCompletedYes400421/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    void verifyGenerateSolsGopAuthenticatedTranslationRequestInApplication() throws IOException {
        Response fullResponse = validatePostRequestSuccessForLegalStatement(
                "/nextsteps/authenticatedTranslation/nextSteps.json", Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        assertTrue(response.contains("an authenticated translation of the will in English or Welsh"));
    }

    @Test
    void verifyEmptyDeceasedFirstNameReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"deceasedForenames\": \"deceasedFirstName\"",
            "\"deceasedForenames\": \"\"", "caseDetails.data.deceasedForenames");
    }

    @Test
    void verifyEmptyDeceasedSurNameReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"deceasedSurname\": \"deceasedLastName\"",
            "\"deceasedSurname\": \"\"", "caseDetails.data.deceasedSurname");
    }

    @Test
    void verifyEmptySolicitorFirmNameReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"solsSolicitorFirmName\": \"SolicitorFirmName\"",
            "\"solsSolicitorFirmName\": \"\"", "caseDetails.data.solsSolicitorFirmName");
    }

    @Test
    void verifyEmptySolicitorSOTForenamesReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"solsSOTForenames\": \"Solicitor_fn\"",
            "\"solsSOTForenames\": \"\"", "caseDetails.data.solsSOTForenames");
    }

    @Test
    void verifyEmptySolicitorSOTSurnameReturnsError() throws IOException {
        validatePostRequestFailureForLegalStatement("\"solsSOTSurname\": \"Solicitor_ln\"", "\"solsSOTSurname\": \"\"",
            "caseDetails.data.solsSOTSurname");
    }

    @Test
    void verifyEmptySolicitorFirmAddressLine1ReturnsError() throws IOException {
        verifyAll(VALIDATE_URL, "failure.missingSolicitorAddressLine1.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.addressLine1");
    }

    @Test
    void verifyEmptySolicitorFirmPostcodeReturnsError() throws IOException {
        verifyAll(VALIDATE_URL, "failure.missingSolicitorPostcode.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.postCode");
    }

    @Test
    void verifyGenerateSolsGopTcResolutionLodgedWithinApplication() throws IOException {
        String dir = "/nextsteps/tcResolutionLodged/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextsteps.json",
                Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        assertTrue(response.contains("a certified copy of the resolution"));
    }

    private String transformCase(String jsonFileName, String path) throws IOException {

        final Response jsonResponse = RestAssured.given()
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(path).andReturn();

        return jsonResponse.getBody().asString();
    }

    private Response validatePostRequestSuccessForLegalStatement(List<String> validationStrings) throws IOException {
        return validatePostRequestSuccessForLegalStatement("success.nextsteps.json", validationStrings);
    }

    private Response validatePostRequestSuccessForLegalStatement(String file, String... validationString)
        throws IOException {
        final var vars = new ArrayList<String>();
        for (final String val : validationString) {
            vars.add(val);
        }
        return validatePostRequestSuccessForLegalStatement(file, vars);
    }

    private Response validatePostRequestSuccessForLegalStatement(String file, List<String> validationString)
        throws IOException {
        String jsonBody = utils.getJsonFromFile(file);
        assertNotNull(jsonBody);
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithSolicitorUser())
            .body(jsonBody)
            .post("/nextsteps/confirmation");

        assertEquals(200, response.getStatusCode());
        final String responseString = response.getBody().asString();
        for (final String val : validationString) {
            assertTrue(responseString.contains(val));
        }
        return response;
    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString,
                                                             String errorMsg) throws IOException {
        Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(replaceString(oldString, replacingString))
            .post(VALIDATE_URL);
        assertEquals(400, response.getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), "Invalid payload");
        assertTrue(response.getBody().asString().contains(errorMsg));
    }

    private String replaceString(String oldJson, String newJson) throws IOException {
        return utils.getJsonFromFile("success.nextsteps.json").replace(oldJson, newJson);
    }

    private void verifyAll(String url, String jsonInput, int statusCode, String message, String fieldError)
        throws IOException {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeadersWithCaseworkerUser())
            .body(utils.getJsonFromFile(jsonInput))
            .post(url);
        assertEquals(statusCode, response.getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), message);
        assertTrue(response.getBody().asString().contains(fieldError));
    }
}
