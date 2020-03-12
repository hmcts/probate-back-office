package uk.gov.hmcts.probate.functional.nextsteps;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.serenitybdd.rest.SerenityRest.given;


@RunWith(SerenityRunner.class)
public class SolCcdServiceNextStepsTests extends IntegrationTestBase {

    @Test
    public void verifyDeceasedFirstNameInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("deceasedFirstName");
    }

    @Test
    public void verifyDeceasedLastNameInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("deceasedLastName");
    }

    @Test
    public void verifyDODInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("01/01/2018");
    }

    @Test
    public void verifySolReferenceInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("refCYA2");
    }

    @Test
    public void verifyIHTFormIdInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("IHT205");
    }

    @Test
    public void verifySolicitorFirmNameInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("SolicitorFirmName");
    }

    @Test
    public void verifySolicitorSOTNameInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("Solicitor_fn Solicitor_ln");
    }

    @Test
    public void verifySolicitorSOTJobTitleInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("TestSOTJobTitle");
    }

    @Test
    public void verifySolicitorSolicitorFirmPostcodeInTheReturnedMarkdown() {
        validatePostRequestSuccessForLegalStatement("firmpc");
    }

    @Test
    public void verifyEmptyDeceasedFirstNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedForenames\": \"deceasedFirstName\"", "\"deceasedForenames\": \"\"", "caseDetails.data.deceasedForenames");
    }

    @Test
    public void verifyEmptyDeceasedSurNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"deceasedSurname\": \"deceasedLastName\"", "\"deceasedSurname\": \"\"", "caseDetails.data.deceasedSurname");
    }

    @Test
    public void verifyEmptySolicitorFirmNameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSolicitorFirmName\": \"SolicitorFirmName\"", "\"solsSolicitorFirmName\": \"\"", "caseDetails.data.solsSolicitorFirmName");
    }

    @Test
    public void verifyEmptySolicitorIHTFormIdReturnsError() {
        validatePostRequestFailureForLegalStatement("\"ihtFormId\": \"IHT205\"", "\"ihtFormId\": \"\"", "caseDetails.data.ihtFormId");
    }

    @Test
    public void verifyEmptySolicitorSOTForenamesReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTForenames\": \"Solicitor_fn\"", "\"solsSOTForenames\": \"\"", "caseDetails.data.solsSOTForenames");
    }

    @Test
    public void verifyEmptySolicitorSOTSurnameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTSurname\": \"Solicitor_ln\"", "\"solsSOTSurname\": \"\"", "caseDetails.data.solsSOTSurname");
    }

    @Test
    public void verifyEmptySolicitorSOTJobTitleameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTJobTitle\": \"TestSOTJobTitle\"", "\"solsSOTJobTitle\": \"\"", "caseDetails.data.solsSOTJobTitle");
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

    private void validatePostRequestSuccessForLegalStatement(String validationString) {
        Response response = given()
                .relaxedHTTPSValidation()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.nextsteps.json"))
                .post("/nextsteps/confirmation");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(validationString));

    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString, String errorMsg) {
        Response response = given().relaxedHTTPSValidation()
            .headers(utils.getHeadersWithUserId())
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
            .headers(utils.getHeadersWithUserId())
            .body(utils.getJsonFromFile(jsonInput))
            .post(url);
        assertEquals(statusCode, response.getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), message);
        assertTrue(response.getBody().asString().contains(fieldError));
    }
}
