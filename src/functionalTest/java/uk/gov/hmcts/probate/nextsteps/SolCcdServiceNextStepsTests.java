package uk.gov.hmcts.probate.nextsteps;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.IntegrationTestBase;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.serenitybdd.rest.SerenityRest.given;
import static org.hamcrest.Matchers.equalToIgnoringCase;


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
        validatePostRequestSuccessForLegalStatement("TestSOTName");
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
        validatePostRequestFailureForLegalStatement("\"solsIHTFormId\": \"IHT205\"", "\"solsIHTFormId\": \"\"", "caseDetails.data.solsIHTFormId");
    }

    @Test
    public void verifyEmptySolicitorSOTNamedReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTName\": \"TestSOTName\"", "\"solsSOTName\": \"\"", "caseDetails.data.solsSOTName");
    }

    @Test
    public void verifyEmptySolicitorSOTJobTitleameReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSOTJobTitle\": \"TestSOTJobTitle\"", "\"solsSOTJobTitle\": \"\"", "caseDetails.data.solsSOTJobTitle");
    }

    @Test
    public void verifyEmptySolicitorFirmPostcodeReturnsError() {
        validatePostRequestFailureForLegalStatement("\"solsSolicitorFirmPostcode\": \"firmpc\"", "\"solsSolicitorFirmPostcode\": \"\"", "caseDetails.data.solsSolicitorFirmPostcode");
    }

    private void validatePostRequestSuccessForLegalStatement(String validationString) {
        Response response = given()
                .headers(utils.getHeadersWithUserId())
                .body(utils.getJsonFromFile("success.nextsteps.json"))
                .post("/nextsteps/confirmation");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(validationString));

    }

    private void validatePostRequestFailureForLegalStatement(String oldString, String replacingString, String errorMsg) {
        given().headers(utils.getHeaders())
                .body(replaceString(oldString, replacingString))
                .post("/nextsteps/validate").then().statusCode(400)
                .and().body("fieldErrors[0].field", equalToIgnoringCase(errorMsg))
                .and().body("message", equalToIgnoringCase("Invalid payload"));
    }

    private String replaceString(String oldJson, String newJson) {
        return utils.getJsonFromFile("success.nextsteps.json").replace(oldJson, newJson);
    }
}
