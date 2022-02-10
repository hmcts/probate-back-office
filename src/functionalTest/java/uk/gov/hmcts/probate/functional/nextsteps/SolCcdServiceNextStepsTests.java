package uk.gov.hmcts.probate.functional.nextsteps;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


@RunWith(SpringIntegrationSerenityRunner.class)
public class SolCcdServiceNextStepsTests extends IntegrationTestBase {
    private static final String VALIDATE_URL = "/nextsteps/validate";

    @Test
    public void verifyAllDataInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("success.nextsteps.json",
            "deceasedFirstName",
            "deceasedLastName", "01/01/2018", "refCYA2", "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln",
            "firmpc", "a photocopy of the signed legal statement and declaration");
    }

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatement() {
        String fullResponse = validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded"
                + ".json", "deceasedFirstName", "deceasedLastName", "01/01/2018", "refCYA2",
            "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "firmpc").getBody().asString();
        assertFalse(fullResponse.contains("a photocopy of the signed legal statement and declaration"));
        assertFalse(fullResponse.contains("(PA16)"));
    }

    @Test
    public void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatementWithPA16Form() {
        String fullResponse = validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded-PA16"
                + ".json", "deceasedFirstName", "deceasedLastName", "01/01/2018", "refCYA2",
            "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln", "firmpc", "(PA16)").getBody().asString();
        assertFalse(fullResponse.contains("a photocopy of the signed legal statement and declaration"));
    }
    
    @Test
    public void verifyAllDataInTheReturnedMarkdownForUploadedLegalStatementWithPA17Form() {
        validatePostRequestSuccessForLegalStatement(
            "success.nextsteps-LegalStatementUploaded-PA17"
                + ".json",  "(PA17)");
    }

    @Test
    public void verifyAllDetailsInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement(Arrays.asList("deceasedFirstName", "deceasedLastName",
            "01/01/2018", "refCYA2", "IHT205", "SolicitorFirmName", "Solicitor_fn Solicitor_ln",
            "firmpc", "appref-PAY1"));
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesBeforeSwitchDate() {
        String dir = "/exceptedEstates/ihtEstateBeforeSwitchDate/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesNo() {
        String dir = "/exceptedEstates/ihtEstateCompletedNo/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesCompletedYes207() {
        String dir = "/exceptedEstates/ihtEstateCompletedYes207/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    public void verifyGenerateSolsGopExpectedEstatesCompletedYes400421() {
        String dir = "/exceptedEstates/ihtEstateCompletedYes400421/";
        Response fullResponse = validatePostRequestSuccessForLegalStatement(dir + "nextSteps.json",
            Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        String confirmationExpectedText = utils.getJsonFromFile(dir + "expectedConfirmation.txt");
        assertEquals(confirmationExpectedText, response);
    }

    @Test
    public void verifyGenerateSolsGopAuthenticatedTranslationRequestInApplication() {
        Response fullResponse = validatePostRequestSuccessForLegalStatement(
                "nextsteps/authenticatedTranslation/nextsteps.json", Collections.emptyList());
        String response = fullResponse.getBody().jsonPath().get("confirmation_body");
        response = removeCrLfs(response);
        assertTrue(response.contains("an authenticated translation of the will"));
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
    public void verifyEmptySolicitorFirmAddressLine1ReturnsError() {
        verifyAll(VALIDATE_URL, "failure.missingSolicitorAddressLine1.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.addressLine1");
    }

    @Test
    public void verifyEmptySolicitorFirmPostcodeReturnsError() {
        verifyAll(VALIDATE_URL, "failure.missingSolicitorPostcode.json", 400, "Invalid payload",
            "caseDetails.data.solsSolicitorAddress.postCode");
    }

    private Response validatePostRequestSuccessForLegalStatement(List<String> validationStrings) {
        return validatePostRequestSuccessForLegalStatement("success.nextsteps.json", validationStrings);
    }
    
    private Response validatePostRequestSuccessForLegalStatement(String file, String... validationString) {
        final var vars = new ArrayList<String>();
        for (final String val : validationString) {
            vars.add(val);
        }
        return validatePostRequestSuccessForLegalStatement(file, vars);
    }
    
    private Response validatePostRequestSuccessForLegalStatement(String file, List<String> validationString) {
        final Response response = given()
            .config(config)
            .relaxedHTTPSValidation()
            .headers(utils.getHeaders())
            .body(utils.getJsonFromFile(file))
            .post("/nextsteps/confirmation");

        assertEquals(200, response.getStatusCode());
        final String responseString = response.getBody().asString();
        for (final String val : validationString) {
            assertTrue(responseString.contains(val));
        }
        return response;
    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString,
                                                             String errorMsg) {
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

    private String replaceString(String oldJson, String newJson) {
        return utils.getJsonFromFile("success.nextsteps.json").replace(oldJson, newJson);
    }

    private void verifyAll(String url, String jsonInput, int statusCode, String message, String fieldError) {
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
